package com.mantono.webserver.reflection

import com.mantono.webserver.ValidRequest
import com.mantono.webserver.rest.Resource
import sun.misc.Request
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.Path
import java.util.Arrays.stream
import java.util.stream.Stream
import kotlin.streams.asSequence

private val CLASS: Regex = Regex("\\w+\\.class\\b")
val classPath: List<String> = System.getProperty("java.class.path").split(":".toRegex())

fun findResources(path: List<String> = classPath): Map<Resource, Method>
{
	return  path.stream()
			.parallel()
			.map { File(it).toPath() }
			.filter { Files.exists(it) }
			.map { Files.walk(it) }
			.flatMap { it }
			.filter { isClassFile(it.fileName)}
			.map { asClass(it) }
			.filterNull()
			.map { it.declaredMethods }
			.flatMap(::stream)
			.filter { it.isAnnotationPresent(Resource::class.java) }
			.filter { hasCorrectParameters(it) }
			.map { pairOfResourceMethod(it) }
			.asSequence()
			.toMap()
}

fun pairOfResourceMethod(method: Method): Pair<Resource, Method>
{
	val resource: Resource = method.getAnnotationsByType(Resource::class.java)[0]
	return Pair(resource, method)
}

fun hasCorrectParameters(method: Method): Boolean
{
	val types: Array<out Class<*>> = method.parameterTypes
	val count: Int = method.parameterCount
	return when(count)
	{
		0 -> true
		1 -> return types[0] == Request::class.java || types[0] == ValidRequest::class.java
		else -> false
	}
}

private fun <T> Stream<T?>.filterNull(): Stream<T> = filter { it != null }.map { it!! }
private fun isClassFile(fileName: Path): Boolean = CLASS.containsMatchIn(fileName.toString())

fun asClass(pathClass: Path): Class<*>?
{
	val className = getQualifiedClassName(pathClass)
	return Class.forName(className)
}

private fun getQualifiedClassName(c: Path): String
{
	var className: String = removeClassPath(c.toString()) ?: c.toString()
	className = className.substring(0, className.length - 6)
	className = className.replace("\\/".toRegex(), ".")
	className = className.replaceFirst("\\B\\.".toRegex(), "")
	return className
}

private fun removeClassPath(resource: String): String?
{
	return classPath.firstOrNull {resource.contains(it)}
			?.let {resource.replaceFirst(it.toRegex(), "")}
}