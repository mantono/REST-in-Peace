package com.mantono.webserver;

import java.time.LocalDateTime;
import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.ResponseCode;

public class WebPage implements Response
{
	private final ResponseCode code;
	private final Header header;
	private String body;

	public WebPage()
	{
		this.code = ResponseCode.OK;
		this.body = "";
		this.header = setDefaultHeader();
	}

	public WebPage(final ResponseCode code)
	{
		this.code = code;
		this.body = "";
		this.header = setDefaultHeader();
	}

	public WebPage(final String body)
	{
		this.code = ResponseCode.OK;
		this.body = body;
		this.header = setDefaultHeader();
	}

	protected Header setDefaultHeader()
	{
		Header header = new Header();

		header.set(HeaderField.DATE, LocalDateTime.now().toString());
		header.set(HeaderField.SERVER, "REST-in-Peace");
		header.set(HeaderField.CONTENT_LENGTH, "" + (body.getBytes().length + 3));
		header.set(HeaderField.CONTENT_TYPE, "text/html; charset=utf-8");

		return header;
	}

	@Override
	public ResponseCode getResponseCode()
	{
		return code;
	}

	@Override
	public Header getHeader()
	{
		header.set(HeaderField.CONTENT_LENGTH, "" + (body.getBytes().length + 3));
		return header;
	}

	@Override
	public CharSequence getBody()
	{
		return body;
	}

	public void setBody(String body)
	{
		this.body = body;
	}

}
