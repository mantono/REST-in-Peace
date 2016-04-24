package com.mantono.webserver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mantono.webserver.reflection.ClassParser;
import com.mantono.webserver.reflection.MethodParser;
import com.mantono.webserver.reflection.ResourceFinder;
import com.mantono.webserver.rest.Resource;

public class ConnectionHandler
{
	private final BlockingQueue<Socket> socketQueue;
	private final ThreadPoolExecutor threadPool;
	private final Map<Resource, Method> resources;
	
	public ConnectionHandler(final int threads, final BlockingQueue<Socket> socketQueue) throws ClassNotFoundException, IOException
	{
		this.socketQueue = socketQueue;
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
		this.threadPool = new ThreadPoolExecutor(threads/2+1, threads, 5000, TimeUnit.MILLISECONDS, queue);
		resources = findResources();
		
		for(int i = 0; i < threads; i++)
			threadPool.execute(new RequestResponder(resources, socketQueue));
	}

	public ConnectionHandler(final int threads, final int queueSize) throws ClassNotFoundException, IOException
	{
		this(threads, new ArrayBlockingQueue<Socket>(queueSize));
	}
	
	private Map<Resource, Method> findResources() throws IOException, ClassNotFoundException
	{
		Map<Resource, Method> resourceMap = new HashMap<Resource, Method>();
		ResourceFinder finder = new ResourceFinder();
		final int found = finder.search();
		if(found == 0)
			return resourceMap;
		
		List<Class<?>> classes = finder.getClasses();
		for(Class<?> classX : classes)
		{
			final ClassParser cp = new ClassParser(classX);
			final List<Method> methods = cp.getResourceMethods();
			final MethodParser methodParser = new MethodParser(methods);
			resourceMap.putAll(methodParser.getResources());
		}
		
		return resourceMap;
	}

	public BlockingQueue<Socket> getConnectionQueue()
	{
		return socketQueue;
	}
}
