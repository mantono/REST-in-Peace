package com.mantono.webserver.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.mantono.webserver.rest.Resource;

public class MethodParser
{
	private final static Pattern PARAMETERS = Pattern.compile("(\\%\\w+)");
	private final Collection<Method> methods;

	public MethodParser(Collection<Method> methods)
	{
		this.methods = methods;
	}
	
	public boolean hasCorrectReturnType(Method method)
	{
		final String returnType = "interface com.mantono.webserver.rest.Response";
		return method.getReturnType().toString().equals(returnType);
	}

	public boolean isStatic(Method method)
	{
		return Modifier.isStatic(method.getModifiers());
	}
	
	public boolean isPublic(Method method)
	{
		return Modifier.isPublic(method.getModifiers());
	}

	public boolean hasCorrectArgumentLength(Method method)
	{
		final int methodParams = method.getParameterCount();
		final int resourceParams = countRequestParameters(method);
		return methodParams == resourceParams;
	}

	private Matcher findMatches(Method method)
	{
		Resource resource = method.getDeclaredAnnotation(Resource.class);
		assert resource != null;
		final String uri = resource.value();
		return PARAMETERS.matcher(uri);
	}

	public int countRequestParameters(Method method)
	{
		final Matcher match = findMatches(method);
		int i = 0;

		while(match.find())
			i++;

		return i;
	}

	public Map<Resource, Method> getResources()
	{
		verifyAllMethods();
		Map<Resource, Method> resources = new HashMap<Resource, Method>();
		for(Method method : methods)
		{
			final Resource[] resourceAnnotations = method.getAnnotationsByType(Resource.class);
			for(Resource resource : resourceAnnotations)
				resources.put(resource, method);
		}
		
		return resources;
	}

	private void verifyAllMethods()
	{
		for(Method method : methods)
			isValidMethod(method);
	}
	
	public boolean isValidMethod(final Method method)
	{
		if(!isPublic(method))
			throw new IllegalArgumentException("Method " + method.getName() + " is not public.");
		if(!isStatic(method))
			throw new IllegalArgumentException("Method " + method.getName() + " is not static.");
		if(!hasCorrectReturnType(method))
			throw new IllegalArgumentException("Method " + method.getName() + " does not return a Respone");
		if(!hasCorrectArgumentLength(method))
			throw new IllegalArgumentException("Method " + method.getName() + " has" + method.getParameterCount()
					+ "parameters but resource URI has a different amount of parameters.");
		
		return true;
	}
}
