package com.mantono.webserver.reflection;

import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class ClassParserTest
{
	private Class<?> dummy;
	private ClassParser parser;

	@BeforeAll
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
		assertEquals(5, methods.size(), "Found methods: " + methods.size());
	}	

}
