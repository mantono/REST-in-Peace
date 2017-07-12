package com.mantono.webserver;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.Socket;
import java.util.Map;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.mantono.webserver.rest.Resource;

import static com.mantono.webserver.reflection.ResourceFinderKt.findResources;
import static com.mantono.webserver.reflection.ResourceFinderKt.getClassPath;

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
		resources = findResources(getClassPath());
		System.out.println(resources.size());
		
		for(int i = 0; i < threads; i++)
			threadPool.execute(new RequestResponder(resources, socketQueue));
	}

	public ConnectionHandler(final int threads, final int queueSize) throws ClassNotFoundException, IOException
	{
		this(threads, new ArrayBlockingQueue<Socket>(queueSize));
	}

	public BlockingQueue<Socket> getConnectionQueue()
	{
		return socketQueue;
	}
}
