package com.mantono.webserver;

import com.mantono.webserver.rest.HeaderField;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

public class RequestParser
{
	private final static String HEADER = "(?:GET|POST|PUT|DELETE) ?[\\/\\w%&;+-]+ HTTP\\/\\d.\\d";
	private final Request resource;
	private final Header headerData;
	private final List<String> body;

	public RequestParser(Socket socket) throws IOException, URISyntaxException
	{
		BufferedReader socketStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		this.resource = getRequestedResource(socketStream);
		this.headerData = readHeader(socketStream);
		this.body = readBody(socketStream);
	}

	private List<String> readBody(BufferedReader socketStream) throws IOException
	{
		List<String> b = new LinkedList<>();
		String line;

		do
		{
			line = socketStream.readLine();
			if(line == null)
				break;

			String decoded = URLDecoder.decode(line, "UTF-8");
			b.add(decoded);

		}while(line.length() != 0);

		return b;
	}

	private Header readHeader(BufferedReader socketStream) throws IOException
	{
		final Header header = new Header();
		String line;

		do
		{
			line = socketStream.readLine();
			if(line == null)
				break;
			final String[] headerField = line.split("\\:", 2);
			final HeaderField field = HeaderField.fromString(headerField[0]);
			if(field == null)
			{
				System.out.println("Warning, ignoring unsupported header field " + headerField[0]);
				continue;
			}
			header.set(field, headerField[1].trim());
		}while(line.length() != 0);

		return header;
	}

	private Request getRequestedResource(BufferedReader socketStream) throws URISyntaxException, IOException
	{
		Request tmpResource = null;
		String line = socketStream.readLine();
		if(line != null)
			if(Pattern.matches(HEADER, line))
				tmpResource = createResource(line);

		if(tmpResource == null)
			tmpResource = createResource("GET / HTTP/1.1");

		return tmpResource;
	}

	public Request getResource()
	{
		return resource;
	}

	private Request createResource(String line) throws URISyntaxException
	{
		final String[] resourceData = line.split("\\s");
		final String verb = resourceData[0];
		final String resourceUri = resourceData[1];
		final String protocol = resourceData[2];
		return new Request(verb, resourceUri, headerData);
	}

	public List<String> getBody()
	{
		return body;
	}
}
