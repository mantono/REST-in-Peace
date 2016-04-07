package com.mantono.webserver;

import com.mantono.webserver.rest.Get;
import com.mantono.webserver.rest.Post;

@Controller
public class DummyClass
{
	@Get("/test")
	public static String test()
	{
		return "bah";
	}
	
	@Get("/test/%id")
	public static String test(final int id)
	{
		return "bah";
	}
	
	@Post("/test/%user/%password")
	public static String test(final String user, final String password)
	{
		return "bah";
	}
}
