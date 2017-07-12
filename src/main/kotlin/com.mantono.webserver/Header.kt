package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField
import com.mantono.webserver.security.Cookie
import java.time.LocalDateTime

data class RequestHeader(val fields: Map<HeaderField, String> = emptyMap(),
                         val cookies: Map<String, String> = emptyMap())
{
	operator fun get(field: HeaderField): String? = fields[field]
	operator fun get(cookie: String): String? = cookies[cookie]
}

data class ResponseHeader(private val _fields: Map<HeaderField, String> = emptyMap(),
                          val cookies: Map<String, Cookie> = emptyMap())
{
	val fields: MutableMap<HeaderField, String>
	init
	{
		fields = HashMap(_fields)
		fields.put(HeaderField.DATE, LocalDateTime.now().toString())
		fields.put(HeaderField.SERVER, "REST-in-Peace")
		fields.put(HeaderField.CONTENT_LENGTH, "0")
	}

	fun setBodySize(body: CharSequence): Int
	{
		val size: Int = (body.toString().toByteArray().size + 3)
		fields.put(HeaderField.CONTENT_LENGTH, size.toString())
		return size
	}

	operator fun set(field: HeaderField, value: String)
	{
		fields[field] = value
	}

	operator fun get(field: HeaderField): String? = fields[field]
	operator fun get(cookie: String): Cookie? = cookies[cookie]
}