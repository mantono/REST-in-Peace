package com.mantono.webserver;

import com.mantono.webserver.rest.HeaderField;

import java.util.List;
import java.util.Map;

public class Request
{
	private final Map<HeaderField, String> header;
	private final Map<String, String> uriValues;
	private final List<String> body;

	public Request(Map<HeaderField, String> header, Map<String, String> uriValues, List<String> body)
	{
		this.header = header;
		this.uriValues = uriValues;
		this.body = body;
	}

	public String get(final HeaderField key)
	{
		return header.get(key);
	}

	public String get(final String key)
	{
		return uriValues.get(key);
	}

	public List<String> getBody()
	{
		return body;
	}
}
