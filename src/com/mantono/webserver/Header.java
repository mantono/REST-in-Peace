package com.mantono.webserver;

import com.mantono.webserver.rest.HeaderField;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.EnumMap;
import java.util.Iterator;
import java.util.Map;
import java.util.Map.Entry;

public class Header implements Iterable<Entry<HeaderField, String>>
{
	private final Map<HeaderField, String> fields;
	
	public Header(final Map<HeaderField, String> fields)
	{
		this.fields = fields;
		set(HeaderField.DATE, LocalDateTime.now().toString());
		set(HeaderField.SERVER, "REST-in-Peace");
	}
	
	public Header()
	{
		this(new EnumMap<HeaderField, String>(HeaderField.class));
	}
	
	public String get(HeaderField field)
	{
		return fields.get(field);
	}

	public Map<HeaderField, String> getFields()
	{
		return Collections.unmodifiableMap(fields);
	}

	
	public boolean isSet(HeaderField field)
	{
		return fields.containsKey(field);
	}
	
	public void set(HeaderField field, String value)
	{
		fields.put(field, value);
	}

	public int setBodySize(final String body)
	{
		final int length = body.getBytes().length + 3;
		set(HeaderField.CONTENT_LENGTH, "" + length);
		return length;
	}

	@Override
	public Iterator<Entry<HeaderField, String>> iterator()
	{
		return fields.entrySet().iterator();
	}
}
