package com.mantono.webserver

import com.mantono.webserver.rest.Resource
import java.nio.file.Path

sealed class Request
{
	abstract val header: RequestHeader
}

data class ValidRequest(val resource: Resource,
                        override val header: RequestHeader,
                        val uriValues: Map<String, String>,
                        val queryParameters: Map<String, String>,
                        val body: String): Request()
{
	operator fun get(key: String): String? = uriValues[key]
}

data class ValidStaticRequest(val file: Path,
                              override val header: RequestHeader): Request()

data class InvalidRequest(val requestedResource: String,
                          override val header: RequestHeader): Request()