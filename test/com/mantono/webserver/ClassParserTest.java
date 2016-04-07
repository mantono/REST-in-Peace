package com.mantono.webserver;

import static org.junit.Assert.*;

import java.lang.annotation.Annotation;

import org.junit.Before;
import org.junit.Test;

public class ClassParserTest
{

	@Before
	public void setUp() throws Exception
	{
	}

	@Test
	public void testClassParser() throws ClassNotFoundException
	{
		Class<?> c = Class.forName("com.mantono.webserver.DummyClass");
		assertTrue(c.isAnnotationPresent(Controller.class));

		final Annotation[] annotations = c.getDeclaredAnnotations();
		assertTrue(annotations.length > 0);
	}

}
