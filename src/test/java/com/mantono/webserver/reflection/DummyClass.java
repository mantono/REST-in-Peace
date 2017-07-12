package com.mantono.webserver.reflection;

import com.mantono.webserver.Request;
import com.mantono.webserver.ResponseHeader;
import com.mantono.webserver.WebPage;
import com.mantono.webserver.rest.*;

import java.util.HashMap;

public class DummyClass
{
	@Resource("/test")
	public static Response test0()
	{
		return new WebPage(ResponseCode.ACCEPTED, new ResponseHeader(), "<html><body><h1>HEJ!</h1><body></html>");
	}
	
	@Resource("/test/%id")
	public static Response test1(final Request request)
	{
		final String id = request.getHeader().get("id");
		return new WebPage(ResponseCode.ACCEPTED, new ResponseHeader(),"<html><body>HEJ "+id+"!<body></html>");
	}
	
	@Resource("/hello/%name/%id")
	public static Response test2(final Request request)
	{
		final String name = request.getHeader().get("name");
		final String id = request.getHeader().get("id");
		return new WebPage(ResponseCode.ACCEPTED, new ResponseHeader(),"<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+id+"</em></body></html>");
	}
	
	@Resource("/hello/%name")
	public static Response test3(final Request request)
	{
		final String name = request.getHeader().get("name");
		final String body = "<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+request.getHeader().get(HeaderField.AUTHORIZATION)+"</em></body></html>";
		return new WebPage(ResponseCode.ACCEPTED, new ResponseHeader(), body);
	}
	
	@Resource(verb = Verb.POST, value = "/test/%user/%password")
	public static Response test4(final Request request)
	{
		final String user = request.getHeader().get("user");
		final String password = request.getHeader().get("password");
		return new WebPage(ResponseCode.ACCEPTED, new ResponseHeader(),"<html><body>HEJ!<p><b>"+user+"</b></p><p><em>"+password+"</b></em><body></html>");
	}
}
