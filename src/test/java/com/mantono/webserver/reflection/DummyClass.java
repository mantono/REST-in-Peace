package com.mantono.webserver.reflection;

import com.mantono.webserver.Request;
import com.mantono.webserver.WebPage;
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
	public static Response test1(final Request request)
	{
		final String id = request.get("id");
		return new WebPage("<html><body>HEJ "+id+"!<body></html>");
	}
	
	@Resource("/hello/%name/%id")
	public static Response test2(final Request request)
	{
		final String name = request.get("name");
		final String id = request.get("id");
		return new WebPage("<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+id+"</em></body></html>");
	}
	
	@Resource("/hello/%name")
	public static Response test3(final Request request)
	{
		final String name = request.get("name");
		return new WebPage("<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+request.get(HeaderField.AUTHORIZATION)+"</em></body></html>");
	}
	
	@Resource(verb = Verb.POST, value = "/test/%user/%password")
	public static Response test4(final Request request)
	{
		final String user = request.get("user");
		final String password = request.get("password");
		return new WebPage("<html><body>HEJ!<p><b>"+user+"</b></p><p><em>"+password+"</b></em><body></html>");
	}
}
