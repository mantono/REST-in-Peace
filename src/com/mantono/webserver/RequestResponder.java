package com.mantono.webserver;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.net.URISyntaxException;
import java.util.Map;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

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
				ResponseSender sender = new ResponseSender(socket, response);
				sender.send();
				sender.close();
				
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
		WebPage response = new WebPage(ResponseCode.BAD_REQUEST);
		ResponseSender rs = new ResponseSender(socket, response);
		rs.send();
		rs.close();
	}

	private Response execute(Method method, ResourceRequest resourceRequested, Resource resource) throws IllegalAccessException, IllegalArgumentException, InvocationTargetException
	{
		Class<?>[] parameterTypes = method.getParameterTypes();
		if(parameterTypes.length == 0)
			return (Response) method.invoke(null);
		
		final String[] splitUri = resource.value().split("/");
		final int[] parameterIndex = new int[parameterTypes.length];
	
		int arrayIndex = 0;
		for(int i = 0; i < splitUri.length; i++)
			if(splitUri[i].contains("%"))
				parameterIndex[arrayIndex++] = i;
		
		final String[] parameters = resourceRequested.getParameters(parameterIndex);
		final Object[] parametersAsObject = new Object[parameterIndex.length];
		
		if(containsHeader(parameterTypes))
		{
			final int hIndex = indexOfHeaderParameter(parameterTypes);
			parametersAsObject[hIndex] = resourceRequested.getHeader();
		}
		
		insertResourceParameters(parameterTypes, parameters, parametersAsObject);
		
		return (Response) method.invoke(null, parametersAsObject);
	}

	private void insertResourceParameters(Class<?>[] parameterTypes, String[] parameters, Object[] parametersAsObject)
	{
		int n = 0;
		for(int i = 0; i < parameterTypes.length; i++)
		{
			if(parametersAsObject[i] != null)
				continue;
			
			final Class<?> type = parameterTypes[i];
			if(type.getName().equals("double"))
				parametersAsObject[i] = Double.parseDouble(parameters[n]);
			else if(type.getName().equals("double"))
				parametersAsObject[i] = Float.parseFloat(parameters[n]);
			else if(type.getName().equals("int"))
				parametersAsObject[i] = Integer.parseInt(parameters[n]);
			else if(type.getName().equals("long"))
				parametersAsObject[i] = Long.parseLong(parameters[n]);
			else
				parametersAsObject[i] = parameters[n];
			
			n++;
		}
	}

	private int indexOfHeaderParameter(Class<?>[] parameterTypes)
	{
		for(int i = 0; i < parameterTypes.length; i++)
			if(typeIsHeader(parameterTypes[i]))
				return i;
		return -1;
	}

	private boolean containsHeader(Class<?>[] parameterTypes)
	{
		for(Class<?> classType : parameterTypes)
			if(typeIsHeader(classType))
				return true;
		return false;
	}
	
	private boolean typeIsHeader(final Class<?> type)
	{
		Class<Header> header = Header.class;
		return type.equals(header);
	}

}
