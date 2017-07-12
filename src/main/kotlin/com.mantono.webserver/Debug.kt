package com.mantono.webserver

import com.mantono.webserver.rest.*

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

@Resource(verb = Verb.POST, value = "/test/post")
fun testPost(request: ValidRequest): Response
{
	return WebPage(body = "<p>Got: ${request.body}<p/>")
}