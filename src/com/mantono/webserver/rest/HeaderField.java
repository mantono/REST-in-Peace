package com.mantono.webserver.rest;

public enum HeaderField
{
	DATE("Date"),
	SERVER("Server"),
	HOST("Host"),
	LAST_MODIFIED("Last-Modified"),
	ETAG("ETag"),
	EXPIRES("Expires"),
	ACCEPT("Accept"),
	ACCEPT_RANGES("Accept-Ranges"),
	ACCEPT_ENCODING("Accept-Encoding"),
	ACCEPT_CHARSET("Accept-Charset"),
	COOKIE("Cookie"),
	SET_COOKIE("Set-Cookie"),
	CONTENT_LENGTH("Content-Length"),
	CONTENT_TYPE("Content-Type"),
	LOCATION("Location"),
	CONNECTION("Connection"),
	KEEP_ALIVE("Keep-Alive"),
	USER_AGENT("User-Agent"),
	REFRESH("Refresh");
	
	private final String name;
	
	private HeaderField(final String name)
	{
		this.name = name;
	}

	public String getName()
	{
		return name;
	}
	
	public static HeaderField fromString(final String input)
	{
		if(input != null)
			for(HeaderField field : values())
				if(field.getName().equalsIgnoreCase(input))
					return field;
		
		return null;
	}
}
