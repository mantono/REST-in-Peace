package com.mantono.webserver.reflection;

import org.junit.jupiter.api.Test;

import java.io.IOException;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class ClassFinderTest
{	
	@Test
	public void testSearchWithClassPath() throws IOException, ClassNotFoundException
	{
		final ResourceFinder cf = new ResourceFinder();
		final int found = cf.search();
		System.out.println(cf.getClasses());
		assertEquals(1, found);
	}

}
