package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField
import com.mantono.webserver.security.Cookie
import java.time.LocalDateTime

data class RequestHeader(val fields: Map<HeaderField, String>,
                         val cookies: Map<String, String> = emptyMap())
{
	operator fun get(field: HeaderField): String? = fields[field]
	operator fun get(cookie: String): String? = cookies[cookie]
}

data class ResponseHeader(private val _fields: Map<HeaderField, String>,
                  val cookies: Map<String, Cookie> = emptyMap(),
                  private val _body: String = "")
{
	val fields: Map<HeaderField, String>
	init
	{
		fields = _fields + mapOf(
				HeaderField.DATE to LocalDateTime.now().toString(),
				HeaderField.SERVER to "REST-in-Peace",
				HeaderField.CONTENT_LENGTH to bodySize())
	}

	private fun bodySize(): String = (_body.toByteArray().size + 3).toString()

	operator fun get(field: HeaderField): String? = fields[field]
	operator fun get(cookie: String): Cookie? = cookies[cookie]
}