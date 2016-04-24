package com.mantono.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

public class SocketListener
{
	private final int port;
	private final BlockingQueue<Socket> clientQueue;
	private final ConnectionHandler connections;
	
	public SocketListener(int port) throws ClassNotFoundException, IOException
	{
		this.port = port;
		this.clientQueue = new ArrayBlockingQueue<Socket>(500);
		this.connections = new ConnectionHandler(12, clientQueue);
	}

	public void listen()
	{
		Thread.currentThread().setName("SocketListener");
		
		try(ServerSocket incomingRequests = new ServerSocket())
		{
			ServerSocket incomingRequests = new ServerSocket();
			incomingRequests.setReuseAddress(true);
			incomingRequests.bind(new InetSocketAddress(port));
			
			while(true)
			{
				Socket connection = incomingRequests.accept();
				clientQueue.offer(connection, 5, TimeUnit.SECONDS);
			}
		}
		catch(IOException | InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		SocketListener sl = new SocketListener(8888);
		sl.listen();
	}

}
