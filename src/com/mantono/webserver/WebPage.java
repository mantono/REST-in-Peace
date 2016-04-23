package com.mantono.webserver;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.HashMap;
import java.util.Map;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.ResponseCode;

public class WebPage implements Response
{
	private final ResponseCode code;
	private final Map<HeaderField, String> header;
	private final String body;
	
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

	private Map<HeaderField, String> setDefaultHeader()
	{
		Map<HeaderField, String> args = new HashMap<HeaderField, String>();
		
		args.put(HeaderField.DATE, LocalDateTime.now().toString());
		args.put(HeaderField.SERVER, "REST-in-Peace");
		args.put(HeaderField.CONTENT_LENGTH, "" + body.length()*2);
		args.put(HeaderField.CONTENT_TYPE, "text/html");
		
		return args;
	}

	@Override
	public ResponseCode getResponseCode()
	{
		return code;
	}

	@Override
	public Map<HeaderField, String> getHeader()
	{
		return header;
	}

	@Override
	public CharSequence getBody()
	{
		return body;
	}

}
