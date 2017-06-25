package com.mantono.webserver.reflection;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import com.mantono.webserver.Request;
import com.mantono.webserver.ValidRequest;
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
		{
			if(method.isAnnotationPresent(Resource.class))
			{
				final Class<?>[] parameterTypes = method.getParameterTypes();
				if(parameterTypes.length == 0)
				{
					resources.add(method);
					continue;
				}
				if (parameterTypes.length > 1)
					throw new IllegalArgumentException("Found method " + method + " that has " + parameterTypes.length + " parameters, but only one is allowed");
				if (parameterTypes[0] != Request.class && parameterTypes[0] != ValidRequest.class)
					throw new IllegalArgumentException("Found method " + method + " that has parameter that is not a Request or ValidRequest (" + parameterTypes[0] + ")");
				resources.add(method);
			}
		}
		
		return resources;
	}
}
