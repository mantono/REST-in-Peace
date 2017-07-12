package com.mantono.webserver.rest

import com.mantono.webserver.ResponseHeader

data class SimpleResponse(override val responseCode: ResponseCode = ResponseCode.OK,
                          override val header: ResponseHeader = ResponseHeader(),
                          override val body: CharSequence = "") : Response
{
	init
	{
		header.setBodySize(body.toString())
	}
}