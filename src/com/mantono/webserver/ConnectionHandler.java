package com.mantono.webserver;

import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

public class ConnectionHandler
{
	private final BlockingQueue<Socket> socketQueue;
	private final ThreadPoolExecutor threadPool;
	
	public ConnectionHandler(final int threads, final BlockingQueue<Socket> socketQueue)
	{
		this.socketQueue = socketQueue;
		BlockingQueue<Runnable> queue = new ArrayBlockingQueue<Runnable>(100);
		this.threadPool = new ThreadPoolExecutor(threads/2+1, threads, 5000, TimeUnit.MILLISECONDS, queue);
		threadPool.execute(new RequestResponder(socketQueue));
	}
	
	public ConnectionHandler(final int threads, final int queueSize)
	{
		this(threads, new ArrayBlockingQueue<Socket>(queueSize));
	}
	
	public BlockingQueue<Socket> getConnectionQueue()
	{
		return socketQueue;
	}
}
