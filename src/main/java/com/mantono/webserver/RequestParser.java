package com.mantono.webserver;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Verb;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URISyntaxException;
import java.net.URLDecoder;
import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.regex.Pattern;

import static java.util.Collections.unmodifiableMap;

public final class RequestParser
{
	private final static String HEADER = "(?:GET|POST|PUT|DELETE) ?[\\/\\w%&;+-]+ HTTP\\/\\d.\\d";
	private final Resource resource;
	private final Map<String, String> uriValues;
	private final Header header;
	private final List<String> body;
	private final Method method;

	public RequestParser(Socket socket, Map<Resource, Method> resourceMap) throws IOException, URISyntaxException
	{
		BufferedReader socketStream = new BufferedReader(new InputStreamReader(socket.getInputStream()));
		final String resource = getRequestedResource(socketStream);
		final String[] resourceData = resource.split("\\s");
		final Verb verb = Verb.valueOf(resourceData[0]);
		final String uri = resourceData[1];

		this.header = readHeader(socketStream);

		this.body = readBody(socketStream);

		this.resource = findResource(verb, uri, resourceMap);
		this.method = resourceMap.get(this.resource);

		this.uriValues = mapValues(this.resource, uri);
	}

	public Request getRequest() throws URISyntaxException
	{
		return new Request(header.getFields(), Collections.<String, String>unmodifiableMap(uriValues), Collections.<String>unmodifiableList(body));
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

	private List<String> readBody(BufferedReader socketStream) throws IOException
	{
		List<String> b = new LinkedList<>();
		final String contentType = header.get(HeaderField.CONTENT_TYPE);
		final boolean decode = contentType != null && contentType.equals("application/x-www-form-urlencoded");
		if(!header.isSet(HeaderField.CONTENT_LENGTH))
			return b;
		final int size = Integer.parseInt(header.get(HeaderField.CONTENT_LENGTH));
		final char[] buffer = new char[size];
		int read = -1;

		while(socketStream.ready())
		{
			final Instant start = Instant.now();
			read = socketStream.read(buffer, 0, size);
			if(read == -1)
				break;
			String line = new String(buffer);
			if(decode)
				line = URLDecoder.decode(line, "UTF-8");
			b.add(line);
			System.out.println(Duration.between(start, Instant.now()));
		}

		return b;
	}

	private String getRequestedResource(BufferedReader socketStream) throws IOException
	{
		String line = socketStream.readLine();
		if(line != null)
			if(Pattern.matches(HEADER, line))
				return line;

		return "GET / HTTP/1.1";
	}

	public Resource getResource()
	{
		return resource;
	}

	private Resource findResource(Verb verb, String uri, Map<Resource, Method> resourceMap)
	{
		for(Resource resourceInMap : resourceMap.keySet())
			if(matchesResource(verb, uri, resourceInMap))
				return resourceInMap;
		throw new IllegalArgumentException("Found no matching resource for " + verb + " " + uri);
	}

	private boolean matchesResource(Verb verb, String uri, Resource resource)
	{
		if(resource.verb() != verb)
			return false;
		return matchesUri(resource.value(), uri);
	}

	private boolean matchesUri(String value, String uri)
	{
		final String[] requestUri = uri.toString().split("/");
		final String[] resourceUri = value.split("/");
		if(requestUri.length != resourceUri.length)
			return false;

		for(int i = 0; i < requestUri.length; i++)
			if(!requestUri[i].equals(resourceUri[i]))
				if(!resourceUri[i].substring(0, 1).equals("%"))
					return false;

		return true;
	}

	private Map<String,String> mapValues(Resource request, String uriMapping)
	{
		final String formal[] = request.value().split("/");
		final String actual[] = uriMapping.split("/");
		if(formal.length != actual.length)
			throw new IllegalArgumentException("Argument lengths is different: "+formal.length+ " and "+actual.length+".");

		Map<String, String> map = new HashMap<>(formal.length);

		for(int i = 0; i < formal.length; i++)
		{
			if(formal[i].length() < 2 || formal[i].charAt(0) != '%')
				continue;

			final String key = formal[i].substring(1);
			final String value = actual[i];
			map.put(key, value);
		}

		return unmodifiableMap(map);
	}

	private String getFormalAnnotationArgs(Method method)
	{
		final Resource r = method.getAnnotation(Resource.class);
		if(r == null)
			throw new IllegalArgumentException("Missing annotation Resource for method " + method);
		return r.value();
	}

	public List<String> getBody()
	{
		return body;
	}

	Method getMethod()
	{
		return method;
	}
}
