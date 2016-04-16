package com.mantono.webserver.reflection;

import static org.junit.Assert.*;

import java.lang.reflect.Method;
import java.util.List;

import org.junit.Before;
import org.junit.Test;

import com.mantono.webserver.reflection.ClassParser;

public class ClassParserTest
{
	private Class<?> dummy;
	private ClassParser parser;

	@Before
	public void setUp() throws Exception
	{
		dummy = Class.forName("com.mantono.webserver.reflection.DummyClass");
		parser = new ClassParser(dummy);
	}

	@Test
	public void testHasResources() throws ClassNotFoundException
	{
		assertTrue(parser.hasResources());
	}
	
	@Test
	public void testGetResourceMethods() throws ClassNotFoundException
	{
		final List<Method> methods = parser.getResourceMethods();
		System.out.println(methods);
		assertTrue(methods.size() == 3);
	}	

}
