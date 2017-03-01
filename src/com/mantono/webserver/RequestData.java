package com.mantono.webserver;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Verb;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

public class RequestData
{
	private final Verb verb;
	private final Map<HeaderField, String> header;
	private final Map<String, String> uriValues;
	private final Map<String, String> body;
	private final List<String> rawBody;

	public RequestData(Request request, final Method method)
	{
		this.verb = request.getVerb();
		this.header = request.getHeader().getFields();
		final String uriMapping = getFormalAnnotationArgs(method);
		this.uriValues = mapValues(request, uriMapping);
		this.body = parseBody(request.)
	}

	private String getFormalAnnotationArgs(Method method)
	{
		final Resource r = method.getAnnotation(Resource.class);
		if(r == null)
			throw new IllegalArgumentException("Missing annotation Resource for method " + method);
		return r.value();
	}

	private Map<String,String> mapValues(Request request, String uriMapping)
	{
		final String formal[] = uriMapping.split("/");
		final String actual[] = request.getUri().toString().split("/");
		if(formal.length != actual.length)
			throw new IllegalArgumentException("Argument lengths is different: "+formal.length+ " and "+actual.length+".");

		Map<String, String> map = new HashMap<>(formal.length);

		for(int i = 0; i < formal.length; i++)
		{
			if(formal[i].length() < 2 || formal[i].charAt(0) != '%')
				continue;

			final String key = formal[i].substring(1);
			final String value = actual[i];
			map.put(key, value);
		}

		return Collections.unmodifiableMap(map);
	}

	public String getValue(String field)
	{
		if(!uriValues.containsKey(field))
			throw new IllegalArgumentException("No value for key \""+field+"\"");
		return uriValues.get(field);
	}

	public String getValue(HeaderField field)
	{
		if(!header.containsKey(field))
			throw new IllegalArgumentException("No value for key \""+field+"\"");
		return header.get(field);
	}
}
