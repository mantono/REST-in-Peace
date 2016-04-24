package com.mantono.webserver.rest;

import com.mantono.webserver.Header;

public interface Response
{
	ResponseCode getResponseCode();
	Header getHeader();
	CharSequence getBody();
}
