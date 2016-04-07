package com.mantono.webserver;

import com.mantono.webserver.rest.Delete;
import com.mantono.webserver.rest.Get;
import com.mantono.webserver.rest.Post;
import com.mantono.webserver.rest.Put;

public class ClassParser
{
	private final Class<?> loadedClass;
	
	public ClassParser(final Class<?> inspectingClass)
	{
		this.loadedClass = inspectingClass;
	}
	
	public boolean isController()
	{
		return loadedClass.isAnnotationPresent(Controller.class);
	}
	
	public boolean hasRestMethods()
	{
		final boolean hasGet = loadedClass.isAnnotationPresent(Get.class);
		final boolean hasPut = loadedClass.isAnnotationPresent(Put.class);
		final boolean hasPost = loadedClass.isAnnotationPresent(Post.class);
		final boolean hasDelete = loadedClass.isAnnotationPresent(Delete.class);
		
		return hasGet || hasPut || hasPost || hasDelete;
	}
	
	
}
