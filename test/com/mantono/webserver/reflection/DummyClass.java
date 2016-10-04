package com.mantono.webserver.reflection;

import com.mantono.webserver.Header;
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
	public static Response test1(final int id)
	{
		return new WebPage("<html><body>HEJ "+id+"!<body></html>");
	}
	
	@Resource("/hello/%name/%id")
	public static Response test1(final String name, final int id, final Header header)
	{
		return new WebPage("<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+id+"</em></body></html>");
	}
	
	@Resource("/hello/%name")
	public static Response test1(final Header header, final String name)
	{
		return new WebPage("<html><head><title>Welcome "+name+"</title></head><body>Hello <strong>"+name.toUpperCase()+"</strong>! - <em>"+header.get(HeaderField.AUTHORIZATION)+"</em></body></html>");
	}
	
	@Resource(verb = Verb.POST, value = "/test/%user/%password")
	public static Response test2(final String user, final String password)
	{
		return new WebPage("<html><body>HEJ!<p><b>"+user+"</b></p><p><em>"+password+"</b></em><body></html>");
	}
}
