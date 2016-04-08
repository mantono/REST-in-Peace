package com.mantono.webserver.reflection;

import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Verb;

public class DummyClass
{
	@Resource("/test")
	public static String test0()
	{
		return "bah";
	}
	
	@Resource("/test/%id")
	public static String test1(final int id)
	{
		return "bah";
	}
	
	@Resource(verb = Verb.POST, value = "/test/%user/%password")
	public static String test2(final String user, final String password)
	{
		return "bah";
	}
}
