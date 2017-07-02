package com.mantono.webserver.reflection

import com.mantono.webserver.ValidRequest
import com.mantono.webserver.rest.ContentType
import com.mantono.webserver.rest.Resource
import com.mantono.webserver.rest.Verb
import sun.misc.Request
import java.io.File
import java.lang.reflect.Method
import java.nio.file.Files
import java.nio.file.Path
import java.util.Arrays.stream
import java.util.stream.Stream
import kotlin.streams.asSequence
import kotlin.streams.toList

private val CLASS: Regex = Regex("\\w+\\.class\\b")
val classPath: List<String> = System.getProperty("java.class.path").split(":".toRegex())

val staticResources: Map<String, Path> by lazy {

	val resourceRegex: Regex = Regex("/resources(/|$)")
	val resourceDirs: List<String> = classPath.filter { resourceRegex.containsMatchIn(it) }

	resourceDirs
			.map { File(it).toPath() }
			.map { Files.walk(it)}
			.flatMap { it.toList() }
			.map { withoutPath(it, resourceDirs) }
			.filter { it.first.isNotEmpty() }
			.toMap()
}

fun withoutPath(entirePath: Path, resourceDirs: List<String>): Pair<String, Path>
{
	val root: String = resourceDirs.firstOrNull{ it in entirePath.toString() }!!
	val subPath: String = entirePath.toString().removePrefix(root)
	return Pair(subPath, entirePath)
}

fun findResources(path: List<String> = classPath): Map<Resource, Method>
{
	return  methodsByAnnotation(Resource::class.java, path)
			.filter { hasCorrectParameters(it) }
			.map { pairOfResourceMethod(it) }
			.asSequence()
			.toMap()
}

fun <T : Annotation> methodsByAnnotation(annotation: Class<T>, path: List<String> = classPath): Stream<Method>
{
	return classesInPath(path)
			.map { it.declaredMethods }
			.flatMap(::stream)
			.filter { it.isAnnotationPresent(annotation) }
}

fun classesInPath(path: List<String> = classPath): Stream<Class<*>>
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