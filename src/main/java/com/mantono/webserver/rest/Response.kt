package com.mantono.webserver.rest

import com.mantono.webserver.ResponseHeader

interface Response
{
	val responseCode: ResponseCode
	val header: ResponseHeader
	val body: CharSequence
}
