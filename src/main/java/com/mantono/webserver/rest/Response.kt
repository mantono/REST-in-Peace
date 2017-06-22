package com.mantono.webserver.rest

import com.mantono.webserver.Header

interface Response
{
	val responseCode: ResponseCode
	val header: Header
	val body: CharSequence
}
