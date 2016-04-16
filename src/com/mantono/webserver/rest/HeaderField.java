package com.mantono.webserver.rest;

public enum HeaderField
{
	DATE("Date"),
	SERVER("Server"),
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
}
