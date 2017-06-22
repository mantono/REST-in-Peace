package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField
import com.mantono.webserver.rest.Response
import com.mantono.webserver.rest.ResponseCode
import com.mantono.webserver.rest.SimpleResponse

class WebPage @JvmOverloads constructor(responseCode: ResponseCode = ResponseCode.OK,
              header: Header = Header(),
              body: CharSequence = "<html></html>") : Response by SimpleResponse(responseCode, header, body)
{
	init
	{
		header.set(HeaderField.CONTENT_TYPE, "text/html; charset=utf-8")
	}
}