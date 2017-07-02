package com.mantono.webserver

import com.mantono.webserver.reflection.staticResources
import com.mantono.webserver.rest.*
import java.io.File
import java.io.IOException
import java.io.InputStream
import java.lang.reflect.InvocationTargetException
import java.lang.reflect.Method
import java.net.Socket
import java.nio.file.Files
import java.nio.file.Path
import java.time.Duration
import java.time.Instant
import java.util.concurrent.BlockingQueue

class RequestResponder(private val resourceMap: Map<Resource, Method>, private val incoming: BlockingQueue<Socket>) : Runnable
{

	override fun run()
	{
		while (true)
		{
			try
			{
				incoming.take().use {socket ->
					val start = Instant.now()
					println("Got a connection!")

					val request = parseRequest(socket, resourceMap)
					val response: Response = when (request)
					{
						is ValidRequest -> createResponse(resourceMap, request)
						is ValidStaticRequest -> createStaticResponse(request)
						is InvalidRequest -> notFound(request)
					}

					send(socket, response)
					val end = Instant.now()
					val duration = Duration.between(start, end)
					println(duration)
				}
			}
			catch (e: InterruptedException)
			{
				// TODO Auto-generated catch block
				e.printStackTrace()
			}
			catch (e: IOException)
			{
				// TODO Auto-generated catch block
				e.printStackTrace()
			}
			catch (e: IllegalAccessException)
			{
				// TODO Auto-generated catch block
				e.printStackTrace()
			}
			catch (e: IllegalArgumentException)
			{
				// TODO Auto-generated catch block
				e.printStackTrace()
			}
			catch (e: InvocationTargetException)
			{
				// TODO Auto-generated catch block
				e.printStackTrace()
			}

		}
	}

	private fun createResponse(resourceMap: Map<Resource, Method>, request: ValidRequest): Response
	{
		val method = resourceMap[request.resource]!!
		return execute(method, request)
	}

	private fun  createStaticResponse(request: ValidStaticRequest): Response
	{
		val loadedFile: CharSequence = loadFile(request.file)
		val fields = mapOf(HeaderField.CONTENT_TYPE to "image/png")
		val header: ResponseHeader = ResponseHeader(fields)
		return SimpleResponse(ResponseCode.OK, header, body = loadedFile)
	}

	private fun loadFile(filePath: Path): CharSequence
	{
		if(!filePath.toFile().exists())
			throw IllegalStateException("File does not exist: $filePath")
		return String(Files.readAllBytes(filePath))
	}

	private fun notFound(request: InvalidRequest): Response
	{
		// TODO Check if there is a default method for invalid request not found
		// should be annotated with @NotFound (to be implemented), else return this response
		return SimpleResponse(ResponseCode.NOT_FOUND)
	}

	private fun execute(method: Method, request: Request): Response
	{
		val parameterTypes = method.parameterTypes

		if (parameterTypes.size == 0)
			return method.invoke(null) as Response
		return method.invoke(null, request) as Response
	}
}