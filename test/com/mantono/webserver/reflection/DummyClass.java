package com.mantono.webserver.reflection;

import com.mantono.webserver.RequestData;
import com.mantono.webserver.WebPage;
import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.Verb;

public class DummyClass
{
	@Resource("/test")
	public static Response test0()
	{
		return new WebPage("<html><body><h1>HEJ!</h1><body></html>");
	}
	
	@Resource("/test/%id")
	public static Response test1(final RequestData request)
	{
		final String id = request.getValue("id");
		return new WebPage("<html><body>HEJ "+id+"!<body></html>");
	}
	
	@Resource("/hello/%name/%id")
	public static Response test2(final RequestData request)
	{
		final String name = request.getValue("name");
		final String id = request.getValue("id");
		return new WebPage("<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+id+"</em></body></html>");
	}
	
	@Resource("/hello/%name")
	public static Response test3(final RequestData request)
	{
		final String name = request.getValue("name");
		return new WebPage("<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+request.getValue(HeaderField.AUTHORIZATION)+"</em></body></html>");
	}
	
	@Resource(verb = Verb.POST, value = "/test/%user/%password")
	public static Response test4(final RequestData request)
	{
		final String user = request.getValue("user");
		final String password = request.getValue("password");
		return new WebPage("<html><body>HEJ!<p><b>"+user+"</b></p><p><em>"+password+"</b></em><body></html>");
	}
}
