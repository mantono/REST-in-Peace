package com.mantono.webserver;

import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

import com.mantono.webserver.rest.HeaderField;

public class Header implements Iterable<Entry<HeaderField, String>>
{
	private final Map<HeaderField, String> fields;
	
	public Header(final Map<HeaderField, String> fields)
	{
		this.fields = fields;
	}
	
	public Header()
	{
		this.fields = new EnumMap<HeaderField, String>(HeaderField.class);
	}
	
	public String get(HeaderField field)
	{
		return fields.get(field);
	}
	
	public boolean isSet(HeaderField field)
	{
		return fields.containsKey(field);
	}
	
	public void set(HeaderField field, String value)
	{
		fields.put(field, value);
	}

	@Override
	public Iterator<Entry<HeaderField, String>> iterator()
	{
		return fields.entrySet().iterator();
	}
}
