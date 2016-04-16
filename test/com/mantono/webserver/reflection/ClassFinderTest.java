package com.mantono.webserver.reflection;

import static org.junit.Assert.*;

import java.io.IOException;
import org.junit.Test;

public class ClassFinderTest
{	
	@Test
	public void testSearchWithClassPath() throws IOException, ClassNotFoundException
	{
		final ResourceFinder cf = new ResourceFinder();
		final int found = cf.search();
		System.out.println(cf.getClasses());
		assertEquals(2, found);
	}

}
