package com.mantono.webserver.rest;

public enum ResponseCode
{
	// https://developer.mozilla.org/en-US/docs/Web/HTTP/Response_codes
	
	CONTINUE(100, "Continue"),
	SWITCHING_PROTOCOL(101, "Switching Protocol"),
	
	OK(200, "OK"),
	CREATED(201, "Created"),
	ACCEPTED(202, "Accepted"),

	SEE_OTHER(303, "See Other"),
	
	BAD_REQUEST(400, "Bad Request"),
	UNAUTHORIZED(401, "Unauthorized"),
	FORBIDDEN(403, "Forbidden"),
	NOT_FOUND(404, "Not Found"),
	
	TEAPOT(418, "I'm a teapot"),
	
	SERVICE_UNAVAILABLE(503, "Service Unavailable");
	
	
	private final short code;
	private final String desc;
	
	private ResponseCode(final int code, final String desc)
	{
		this.code = (short) code;
		this.desc = desc;
	}
	
	public short getCode()
	{
		return code;
	}
	
	public String getDescription()
	{
		return desc;
	}
}
