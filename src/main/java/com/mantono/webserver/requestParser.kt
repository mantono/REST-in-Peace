package com.mantono.webserver

import com.mantono.webserver.rest.*
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Method
import java.net.Socket
import java.util.stream.Stream
import java.time.Instant
import java.util.LinkedList
import java.time.Duration


private val HEADER = Regex("(?:GET|POST|PUT|DELETE) ?[/\\w%=?&;+-.]+ HTTPS?/\\d.\\d")

private fun <T> Stream<T?>.notNull(): Stream<T> = filter { it != null }.map { it!! }

fun parseRequest(socket: Socket, resourceMap: Map<Resource, Method>): Request
{
	val socketStream: BufferedReader = BufferedReader(InputStreamReader(socket.getInputStream()))

	val requestedResource = requestedResource(socketStream)
	System.out.println(requestedResource)
	val header: RequestHeader = readHeader(socketStream)
	val body: String = readBody(socketStream, header[HeaderField.CONTENT_LENGTH], header[HeaderField.CONTENT_TYPE])

	val resourceData: List<String> = requestedResource.split("\\s".toRegex())
	val verb: Verb = Verb.valueOf(resourceData[0])
	val uriAndQuery = resourceData[1].split("\\?".toRegex())
	val uri: String = uriAndQuery[0]
	val queryParameters: Map<String, String> = queryOf(uriAndQuery)
	val resource: Resource? = findResource(verb, uri, resourceMap)

	println(queryParameters)

	return when(resource != null)
	{
		true -> ValidRequest(resource!!, header, mapValues(resource, uri), queryParameters, body)
		false -> InvalidRequest(requestedResource, header)
	}
}

fun queryOf(uriWithquery: List<String>): Map<String, String>
{
	if(uriWithquery.size == 1)
		return emptyMap()

	val query = uriWithquery[1]
	if(query.isNullOrEmpty())
		return emptyMap()

	return stringToMap(query)
}

private fun mapValues(request: Resource, uriMapping: String): Map<String, String>
{
	val formal = request.value.split("/".toRegex())
	val actual = uriMapping.split("/".toRegex())
	if (formal.size != actual.size)
		throw IllegalArgumentException("Argument lengths is different: " + formal.size + " and " + actual.size + ".")

	val map = HashMap<String, String>(formal.size)

	for (i in formal.indices)
	{
		if (formal[i].length < 2 || formal[i].first() != '%') //TODO Simplify to only second part of if?
			continue

		val key = formal[i].substring(1)
		val value = actual[i]
		map.put(key, value)
	}

	return map
}

//TODO Rewrite findResource, matchesResource and matchesUri as a stream/sequence
private fun findResource(verb: Verb, uri: String, resourceMap: Map<Resource, Method>): Resource?
{
	for (resourceInMap in resourceMap.keys)
		if (matchesResource(verb, uri, resourceInMap))
			return resourceInMap
	return null
}

private fun matchesResource(verb: Verb, uri: String, resource: Resource): Boolean
{
	if (resource.verb != verb)
		return false
	return matchesUri(resource.value, uri)
}

private fun matchesUri(value: String, uri: String): Boolean
{
	val requestUri = uri.split("/".toRegex())
	val resourceUri = value.split("/".toRegex())
	if (requestUri.size != resourceUri.size)
		return false

	for(i in requestUri.indices)
		if(requestUri[i] != resourceUri[i])
			if(resourceUri[i].substring(0, 1) != "%")
				return false

	return true
}

fun findFirstEmptyLine(input: List<String>): Int
{
	for(i in input.indices)
		if(input[i].isEmpty())
			return i
	return -1
}

fun parseHeader(input: List<String>, endOfHeader: Int): RequestHeader
{
	val headerFields: MutableMap<HeaderField, String> = HashMap(16)

	input.stream()
			.skip(1L)
			.limit(endOfHeader-1L)
			.map { it.split("\\:".toRegex(), 2) }
			.notNull()
			.filter { isHeaderField(it) }
			.map { asHeaderField(it) }
			.forEach { append(it, headerFields) }

	val cookies: MutableMap<String, String> = HashMap(8)

	input.stream()
			.skip(1L)
			.limit(endOfHeader-1L)
			.filter(::isCookie)
			.map(::readCookieData)
			.forEach { cookies.put(it.first, it.second) }

	return RequestHeader(headerFields, cookies)
}

fun readCookieData(line: String): Pair<String, String>
{
	val data: String = line.split("; ")[0]
	val keyValue: List<String> = data.split("=")
	return Pair(keyValue[0], keyValue[1])
}

fun append(it: Pair<HeaderField, String>, headerFields: MutableMap<HeaderField, String>)
{
	val value: String? = headerFields.putIfAbsent(it.first, it.second)
	if(value != null)
		headerFields.put(it.first, value + "; " + it.second)
}

fun isCookie(field: String): Boolean = field.matches(Regex("^Cookie:"))

fun isHeaderField(line: List<String>): Boolean = HeaderField.fromString(line[0]) != null

fun asHeaderField(line: List<String>): Pair<HeaderField, String>
{
	val field: HeaderField = HeaderField.fromString(line[0])
	val value = line[1].trim()
	return Pair(field, value)
}

private fun readHeader(socketStream: BufferedReader): RequestHeader
{
	val header: MutableMap<HeaderField, String> = HashMap()

	do
	{
		val line: String = socketStream.readLine() ?: break
		val headerField = line.split("\\:".toRegex(), 2).toTypedArray()
		val field = HeaderField.fromString(headerField[0])
		if (field == null)
		{
			println("Warning, ignoring unsupported header field " + headerField[0])
			continue
		}
		header[field] = headerField[1].trim {it <= ' '}
	}
	while(line.isNotEmpty())

	return RequestHeader(header)
}

private fun readBody(socketStream: BufferedReader, size: String?, contentType: String?): String
{
	val bodySize: Int = Integer.parseInt(size ?: "0")
	val type: ContentType = ContentType.from(contentType ?: "unknown")
	val body: CharArray = when(bodySize)
	{
		0 -> CharArray(0)
		else -> retrieveBody(socketStream, bodySize, type)
	}

	val bodyDecoded  = type.decode(body)
	return bodyDecoded
}

private fun retrieveBody(socketStream: BufferedReader, size: Int, type: ContentType): CharArray
{
	val extraPadding: Int = size - (size % type.bufferSize)
	val data = CharArray(size + extraPadding)
	val bufferSize: Int = type.bufferSize.coerceAtMost(size)
	var offset: Int = 0

	while(socketStream.ready())
	{
		val start = Instant.now()
		val read = socketStream.read(data, offset, bufferSize)
		if (read == -1)
			break

		offset += bufferSize
		System.out.println(Duration.between(start, Instant.now()))
		System.out.print(data)
	}

	return data
}

private fun readStream(inputStream: InputStream?): List<String>
{
	val socketStream = BufferedReader(InputStreamReader(inputStream))
	val b = LinkedList<String>()

	val buffer = CharArray(32)

	while (socketStream.ready())
	{
		val read = socketStream.read(buffer, 0, 32)
		if (read == -1)
			break
		var line = String(buffer)
		b.add(line)
	}

	return b
}

private fun requestedResource(socketStream: BufferedReader): String
{
	val line: String = socketStream.readLine()
	return when(HEADER.matches(line))
	{
		true -> line
		false -> throw IllegalArgumentException("Expected resource header, but got: $line")
	}
}

fun stringToMap(input: String, pairDelimiter: String = "[&;]\\w+"): Map<String, String>
{
	return input.split(Regex(pairDelimiter)).asSequence()
			.map { it.split("=") }
			.filter { it.size == 2}
			.map { Pair(it[0], it[1]) }
			.toMap()
}