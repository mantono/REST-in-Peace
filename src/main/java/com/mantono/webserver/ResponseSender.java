package com.mantono.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map;
import java.util.Map.Entry;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.ResponseCode;

public class ResponseSender
{
	private final Socket socket;
	private final Response response;
	
	public ResponseSender(final Socket socket, final Response response)
	{
		this.socket = socket;
		this.response = response;
	}
	
	public int send() throws IOException
	{
		OutputStream socketOut = socket.getOutputStream();
		PrintStream streamOut = new PrintStream(socketOut, true);
		printRespone(streamOut, response);				
		streamOut.flush();
		socketOut.flush();
		final String size = response.getHeader().get(HeaderField.CONTENT_LENGTH); 
		return Integer.parseInt(size);
	}
	
	private void printRespone(PrintStream streamOut, Response response)
	{
		streamOut.append(parseResponseCode(response.getResponseCode()));
		streamOut.append(parseHeader(response.getHeader()));
		streamOut.append(response.getBody().toString());
		streamOut.append("\r\n");
	}
	
	private CharSequence parseResponseCode(ResponseCode responseCode)
	{
		final StringBuilder responseCodeData = new StringBuilder();
		responseCodeData.append("HTTP/1.1 ");
		responseCodeData.append("" + response.getResponseCode().getCode());
		responseCodeData.append(" " + response.getResponseCode().getDescription());
		responseCodeData.append("\r\n");
		return responseCodeData;
	}

	private CharSequence parseHeader(final Header header)
	{
		final StringBuilder headerData = new StringBuilder();
		for(Entry<HeaderField, String> entry : header)
		{
			final HeaderField field = entry.getKey();
			final String value = entry.getValue();
			headerData.append(field.getName() + ": " + value +"\r\n");
		}
		headerData.append("\r\n\r\n");
		return headerData;
	}

	public boolean close() throws IOException
	{
		if(!socket.isClosed())
		{
			socket.close();
			return true;
		}
		else
		{
			return false;
		}
	}
}
