package com.mantono.webserver.reflection;

import com.mantono.webserver.WebPage;
import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.Verb;

public class DummyClass
{
	@Resource("/test")
	public static Response test0()
	{
		return new WebPage("<html><body>HEJ!<body></html>");
	}
	
	@Resource("/test/%id")
	public static Response test1(final int id)
	{
		return new WebPage("<html><body>HEJ "+id+"!<body></html>");
	}
	
	@Resource(verb = Verb.POST, value = "/test/%user/%password")
	public static Response test2(final String user, final String password)
	{
		return new WebPage("<html><body>HEJ!<body></html>");
	}
}
