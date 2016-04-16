package com.mantono.webserver.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mantono.webserver.rest.Resource;

public class ClassParser
{
	private final Class<?> loadedClass;
	
	public ClassParser(final Class<?> inspectingClass)
	{
		this.loadedClass = inspectingClass;
	}
	
	public static boolean hasResources(final Class<?> inspectingClass)
	{
		for(Method method : inspectingClass.getDeclaredMethods())
			if(method.isAnnotationPresent(Resource.class))
				return true;
		return false;
	}
	
	public boolean hasResources()
	{
		return getResourceMethods().size() > 0;
	}
	
	public List<Method> getResourceMethods()
	{
		List<Method> resources = new ArrayList<Method>();
		for(Method method : loadedClass.getDeclaredMethods())
			if(method.isAnnotationPresent(Resource.class))
				resources.add(method);
		
		return resources;
	}
}
