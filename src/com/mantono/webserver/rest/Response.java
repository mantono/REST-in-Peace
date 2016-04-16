package com.mantono.webserver.rest;

import java.util.Map;

public interface Response
{
	ResponseCode getResponseCode();
	Map<HeaderField, String> getHeader();
	CharSequence getBody();
}
