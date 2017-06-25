package com.mantono.webserver.reflection;

import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Method;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.fail;

public class MethodParserTest
{
	@Test
	public void testCountRequestParameters() throws ClassNotFoundException
	{
		Class<?> dummy = Class.forName("com.mantono.webserver.reflection.DummyClass");
		ClassParser cParser = new ClassParser(dummy);
		List<Method> methods = cParser.getResourceMethods();
		MethodParser mParser = new MethodParser(methods);
		System.out.println(mParser.getResources());
	}
	
	@Disabled
	@Test
	public void testBadMethodParameters() throws ClassNotFoundException
	{
		Class<?> dummy = Class.forName("com.mantono.webserver.reflection.BadResourceClass");
		ClassParser cParser = new ClassParser(dummy);
		List<Method> methods = cParser.getResourceMethods();
		MethodParser mParser = new MethodParser(methods);
		assertThrows(IllegalArgumentException.class, ()->{mParser.getResources();});
	}

	@Disabled
	@Test
	public void testGetResources()
	{
		fail("Not yet implemented");
	}

}
