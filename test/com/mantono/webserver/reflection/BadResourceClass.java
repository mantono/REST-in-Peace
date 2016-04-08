package com.mantono.webserver.reflection;

import com.mantono.webserver.rest.Resource;

public class BadResourceClass
{
	@Resource("/test/%arg0/%arg1")
	public static String badMethod(int arg0, int arg1, int arg2)
	{
		return "";
	}
}
