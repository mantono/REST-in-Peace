package com.mantono.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.Map.Entry;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Response;

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
		streamOut.append("HTTP/1.1 ");
		streamOut.append("" + response.getResponseCode().getCode());
		streamOut.append(" " + response.getResponseCode().getDescription());
		streamOut.append("\r\n");
		//streamOut.append("Content-Type: text/html; charset=utf-8");
		for(Entry<HeaderField, String> entry : response.getHeader().entrySet())
		{
			final HeaderField field = entry.getKey();
			final String value = entry.getValue();
			streamOut.append(field.getName() + ": " + value +"\r\n");
		}
		streamOut.append("\r\n\r\n");
		streamOut.append(response.getBody().toString());
		streamOut.append("\r\n");
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
