package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField
import com.mantono.webserver.rest.Resource
import com.mantono.webserver.rest.Response
import com.mantono.webserver.rest.ResponseCode

fun main(args: Array<String>)
{
	Server.start(arrayOf("8080"))
}

@Resource("/test1")
fun test(): Response
{
	val body = "<html><body><h1>Server Test</h1><p>If you see this, then everything works.</p><body></html>"
	return WebPage(ResponseCode.ACCEPTED, ResponseHeader(), body)
}

@Resource("/test2")
fun test2(request: ValidRequest): Response
{
	val body = "<html><body><h1>Hello ${request.header[HeaderField.USER_AGENT]}</h1><body></html>"
	return WebPage(ResponseCode.ACCEPTED, ResponseHeader(), body)
}

/*
@Resource("/test3")
fun test3(str: String): Response
{
	val body = "<html><body><h1>Hello $str</h1><body></html>"
	return WebPage(ResponseCode.ACCEPTED, ResponseHeader(), body)
}
*/