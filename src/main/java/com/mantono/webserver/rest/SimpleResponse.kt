package com.mantono.webserver.rest

import com.mantono.webserver.Header

data class SimpleResponse(override val responseCode: ResponseCode = ResponseCode.OK,
                          override val header: Header = Header(),
                          override val body: CharSequence) : Response
{
	init
	{
		header.setBodySize(body.toString())
	}
}