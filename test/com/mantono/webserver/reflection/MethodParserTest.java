package com.mantono.webserver.reflection;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

public class MethodParserTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testCountRequestParameters() throws ClassNotFoundException
	{
		Class<?> dummy = Class.forName("com.mantono.webserver.reflection.DummyClass");
		ClassParser cParser = new ClassParser(dummy);
		List<Method> methods = cParser.getResourceMethods();
		MethodParser mParser = new MethodParser(methods);
		System.out.println(mParser.getResources());
	}
	
	@Ignore
	@Test(expected=IllegalArgumentException.class)
	public void testBadMethodParameters() throws ClassNotFoundException
	{
		Class<?> dummy = Class.forName("com.mantono.webserver.reflection.BadResourceClass");
		ClassParser cParser = new ClassParser(dummy);
		List<Method> methods = cParser.getResourceMethods();
		MethodParser mParser = new MethodParser(methods);
		mParser.getResources();
	}

	@Ignore
	@Test
	public void testGetResources()
	{
		fail("Not yet implemented");
	}

}
