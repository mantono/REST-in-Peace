package com.mantono.webserver;

import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mantono.webserver.rest.HeaderField;
import com.mantono.webserver.rest.Resource;
import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.ResponseCode;

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
				if(socket == null)
					continue;
				
				RequestParser request = new RequestParser(socket);
				ResourceRequest requestedResource = request.getResource();
				
				Method method = null;
				Resource resource = null;
				for(Resource resourceInMap : resourceMap.keySet())
				{
					if(requestedResource.matchesResource(resourceInMap))
					{
						method = resourceMap.get(resourceInMap);
						resource = resourceInMap;
						break;
					}
				}
				
				if(method == null || resource == null)
				{
					writeBadRequest(socket);
					continue;
				}
				
				Response response = execute(method, requestedResource, resource);
				
				OutputStream socketOut = socket.getOutputStream();
				PrintStream streamOut = new PrintStream(socketOut, true);
				printRespone(streamOut, response);				
				streamOut.flush();
				socketOut.flush();
				
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

	private void writeBadRequest(final Socket socket) throws IOException
	{
		OutputStream socketOut = socket.getOutputStream();
		PrintStream streamOut = new PrintStream(socketOut, true);
		WebPage response = new WebPage(ResponseCode.BAD_REQUEST);
		printRespone(streamOut, response);
		streamOut.flush();
		socketOut.flush();
	}

	private void printRespone(PrintStream streamOut, Response response)
	{
		streamOut.append("HTTP/1.1 ");
		streamOut.append("" + response.getResponseCode().getCode());
		streamOut.append(" " + response.getResponseCode().getDescription());
		streamOut.append("\r\n");
		streamOut.append("Content-Type: text/html; charset=utf-8");
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

	private Response execute(Method method, ResourceRequest resourceRequested, Resource resource) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		final Class<?>[] parameterTypes = method.getParameterTypes();
		if(parameterTypes.length == 0)
			return (Response) method.invoke(null);
		
		final String[] splitUri = resource.value().split("/");
		final int[] parameterIndex = new int[parameterTypes.length];
	
		int arrayIndex = 0;
		for(int i = 0; i < splitUri.length; i++)
			if(splitUri[i].contains("%"))
				parameterIndex[arrayIndex] = i;
		
		final String[] parameters = resourceRequested.getParameters(parameterIndex);
		final Object[] parametersAsObject = new Object[parameterIndex.length];
		
		for(int i = 0; i < parameterTypes.length; i++)
		{
			final Class<?> type = parameterTypes[i];
			if(type.getName().equals("double"))
				parametersAsObject[i] = Double.parseDouble(parameters[i]);
			else if(type.getName().equals("double"))
				parametersAsObject[i] = Float.parseFloat(parameters[i]);
			else if(type.getName().equals("int"))
				parametersAsObject[i] = Integer.parseInt(parameters[i]);
			else if(type.getName().equals("long"))
				parametersAsObject[i] = Long.parseLong(parameters[i]);
			else
				parametersAsObject[i] = parameters[i];
		}
		
		return (Response) method.invoke(null, parametersAsObject);
	}

}
