package com.mantono.webserver.rest

enum class ContentType(val type: String, val bufferSize: Int, val decode: (b: CharArray) -> String = ::passThrough)
{
	HTML("text/html", 32),
	FORM_DATA("multipart/form-data", 128),
	FORM_URLENCODED("application/x-www-form-urlencoded", 64),
	JSON("application/json", 32);

	companion object
	{
		fun from(type: String): ContentType = values().firstOrNull {Regex(it.type).containsMatchIn(type)} ?: HTML
	}
}

fun passThrough(bytes: CharArray): String = String(bytes)
