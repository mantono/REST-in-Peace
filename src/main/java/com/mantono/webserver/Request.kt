package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField

/**
 * @author Anton &Ouml;sterberg
 */
data class Request(val header: RequestHeader,
              val uriValues: Map<String, String>,
              val body: List<String>)
{
	operator fun get(key: String): String? = uriValues[key]
}