package com.mantono.webserver;

import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Verb;

import java.net.URI;
import java.net.URISyntaxException;

public class Request
{
	private final Verb verb;
	private final URI uri;
	private final Header header;
	
	public Request(final String verb, final String uri, final Header header) throws URISyntaxException
	{
		this.verb = Verb.valueOf(verb);
		this.uri = new URI(uri);
		this.header = header;
	}

	public Verb getVerb()
	{
		return verb;
	}
	
	public URI getUri()
	{
		return uri;
	}
	
	public Header getHeader()
	{
		return header;
	}
	
	public boolean matchesResource(Resource resource)
	{
		if(resource.verb() != this.verb)
			return false;
		return matchesUri(resource.value());
	}

	public boolean matchesUri(String value)
	{
		final String[] requestUri = uri.toString().split("/");
		final String[] resourceUri = value.split("/");
		if(requestUri.length != resourceUri.length)
			return false;
		
		for(int i = 0; i < requestUri.length; i++)
			if(!requestUri[i].equals(resourceUri[i]))
				if(!resourceUri[i].substring(0, 1).equals("%"))
					return false;
		
		return true;
	}

	public String[] getParameters(final int... index)
	{
		final String[] requestUri = uri.toString().split("/");
		final String[] parameters = new String[index.length];
		
		int pIndex = 0;
		
		for(int i = 0; i < requestUri.length; i++)
			if(i == index[pIndex])
				parameters[pIndex++] = requestUri[i];
		
		return parameters;
	}
}
