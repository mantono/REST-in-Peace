package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField
import com.mantono.webserver.rest.Resource
import com.mantono.webserver.rest.Verb
import java.io.BufferedReader
import java.io.InputStream
import java.io.InputStreamReader
import java.lang.reflect.Method
import java.net.Socket
import java.util.stream.Stream
import kotlin.streams.toList
import kotlinx.coroutines.experimental.*


private val HEADER = Regex("(?:GET|POST|PUT|DELETE) ?[/\\w%&;+-.]+ HTTPS?/\\d.\\d")

private fun <T> Stream<T?>.notNull(): Stream<T> = filter { it != null }.map { it!! }

fun parseRequest(socket: Socket, resourceMap: Map<Resource, Method>): Request
{
	launch(commonPool){
		val input: List<String> = readStream(socket.getInputStream())
	}
	val requestedResource = requestedResource(input.first())

	val resourceData: List<String> = requestedResource.split("\\s".toRegex())
	val verb: Verb = Verb.valueOf(resourceData[0])
	val uri: String = resourceData[1]
	val endOfHeader: Int = findFirstEmptyLine(input)
	val header: RequestHeader = parseHeader(input, endOfHeader)

	val body = input.subList(endOfHeader, input.lastIndex)

	return Request(header, uriValues, body)
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

private suspend fun readStream(inputStream: InputStream?): List<String>
{
	val socketStream = BufferedReader(InputStreamReader(inputStream))
	return socketStream.lines()
			.notNull()
			.toList()
}

private fun requestedResource(line: String): String
{
	return when(HEADER.matches(line))
	{
		true -> line
		false -> "GET / HTTP/1.1"
	}
}