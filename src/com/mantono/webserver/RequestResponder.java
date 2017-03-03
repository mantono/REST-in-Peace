package com.mantono.webserver;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.ResponseCode;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URISyntaxException;
import java.time.Duration;
import java.time.Instant;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class RequestResponder implements Runnable
{
	private final Map<Resource, Method> resourceMap;
	private final BlockingQueue<Socket> incoming;
	
	public RequestResponder(final Map<Resource, Method> resources, final BlockingQueue<Socket> requests)
	{
		this.resourceMap = resources;
		this.incoming = requests;
	}

	@Override
	public void run()
	{
		while(true)
		{
			try(final Socket socket = incoming.poll(30, TimeUnit.SECONDS))
			{
				final Instant start = Instant.now();
				if(socket == null)
					continue;

				RequestParser rParser = new RequestParser(socket, resourceMap);
				Request request = rParser.getRequest();

				Response response = execute(rParser.getMethod(), request);
				ResponseSender sender = new ResponseSender(socket, response);
				sender.send();
				sender.close();
				final Instant end = Instant.now();
				final Duration duration = Duration.between(start, end);
				System.out.println(duration);
			}
			catch(InterruptedException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(IOException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(URISyntaxException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(IllegalAccessException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(IllegalArgumentException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			catch(InvocationTargetException e)
			{
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}

	private void notFound(final Socket socket) throws IOException
	{
		WebPage response = new WebPage(ResponseCode.NOT_FOUND);
		response.getHeader().set(HeaderField.CONTENT_LENGTH, "0");
		ResponseSender rs = new ResponseSender(socket, response);
		rs.send();
		rs.close();
	}

	private Response execute(Method method, Request request) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class<?>[] parameterTypes = method.getParameterTypes();

		if(parameterTypes.length == 0)
			return (Response) method.invoke(null);
		if(parameterTypes.length > 1)
			throw new IllegalArgumentException("Found method " + method + " that has parameter than one parameter");
		if(parameterTypes[0] != Request.class)
			throw new IllegalArgumentException("Found method " + method + " that has parameter that is not a Request ("+parameterTypes[0]+")");

		return (Response) method.invoke(null, request);
	}
}