package com.mantono.webserver.reflection;

import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.lang.reflect.Parameter;
import java.util.ArrayList;
import java.util.Collection;
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

	public boolean hasCorrectArgumentNames(Method method)
	{
		Resource resource = method.getDeclaredAnnotation(Resource.class);
		final Matcher matches = findMatches(method);
		Parameter[] parameters = method.getParameters();
		String[] args = getResourceArguments(resource.value(), matches);
		for(int i = 0; i < parameters.length; i++)
		{
			boolean match = false;
			for(int n = 0; n < args.length; n++)
			{
				if(parameters[i].getName().equals(args[n]))
					match = true;
			}
			if(!match)
				return false;
		}
		return true;
	}

	private String[] getResourceArguments(String resourceUri, Matcher matches)
	{
		final List<String> args = new ArrayList<String>();
		while(matches.find())
		{
			final int start = matches.start();
			final int end = matches.end();
			final String arg = resourceUri.substring(start, end);
			final String argPrefixRemoved = arg.replaceAll("\\%", "");
			args.add(argPrefixRemoved);
		}
		return args.toArray(new String[args.size()]);
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
		return null;
	}

	private void verifyAllMethods()
	{
		for(Method method : methods)
		{
			if(!isPublic(method))
				throw new IllegalArgumentException("Method " + method.getName() + " is not public.");
			if(!isStatic(method))
				throw new IllegalArgumentException("Method " + method.getName() + " is not static.");
			if(!hasCorrectArgumentLength(method))
				throw new IllegalArgumentException("Method " + method.getName() + " has" + method.getParameterCount()
						+ "parameters but resource URI has a different amount of parameters.");
			if(!hasCorrectArgumentNames(method))
				throw new IllegalArgumentException("Argument names did not match for method " + method.getName());
		}
	}
}
