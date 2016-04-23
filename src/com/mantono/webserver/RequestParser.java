package com.mantono.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.EnumMap;
import java.util.Map;
import java.util.regex.Pattern;

import com.mantono.webserver.rest.HeaderField;

public class RequestParser
{
	private final static String HEADER = "(?:GET|POST|PUT|DELETE) ?[\\/\\w]+ HTTP\\/\\d.\\d";
	private final Socket socket;
	private ResourceRequest resource;
	private final Map<HeaderField, String> headerData;

	public RequestParser(Socket socket) throws IOException, URISyntaxException
	{
		this.socket = socket;
		this.headerData = new EnumMap<HeaderField, String>(HeaderField.class);
		
		BufferedReader socketStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		String line = socketStream.readLine();
		if(line != null)
			if(Pattern.matches(HEADER, line))
				this.resource = createResource(line);
		
		if(this.resource == null)
			this.resource = createResource("GET / HTTP/1.1");
		
		do
		{
			line = socketStream.readLine();
			if(line == null)
				break;
			final String[] headerField = line.split("\\:", 2);
			final HeaderField field = HeaderField.fromString(headerField[0]);
			if(field == null)
			{
				System.out.println("Warning, ignoring unsupported header field " + field);
				continue;
			}
			headerData.put(field, headerField[1].trim());
		}while(line.length() != 0);
		
	}
	
	public ResourceRequest getResource()
	{
		return resource;
	}

	private ResourceRequest createResource(String line) throws URISyntaxException
	{
		final String[] resourceData = line.split("\\s");
		final String verb = resourceData[0];
		final String resourceUri = resourceData[1];
		final String protocol = resourceData[2];
		return new ResourceRequest(verb, resourceUri);
	}

}
