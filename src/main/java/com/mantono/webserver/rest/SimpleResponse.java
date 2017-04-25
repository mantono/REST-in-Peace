package com.mantono.webserver.rest;

import com.mantono.webserver.Header;

public class SimpleResponse implements Response
{
	private final Header header;
	private final String body;
	private final ResponseCode code;

	public SimpleResponse(final String body)
	{
		this(body, ResponseCode.OK);
	}

	public SimpleResponse(final String body, final ResponseCode code)
	{
		this.body = body;
		this.code = code;
		this.header = new Header();
		this.header.setBodySize(body);
	}

	@Override
	public ResponseCode getResponseCode()
	{
		return code;
	}

	@Override
	public Header getHeader()
	{
		return header;
	}

	@Override
	public CharSequence getBody()
	{
		return body;
	}
}
