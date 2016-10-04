package com.mantono.webserver;

import java.io.IOException;
import java.net.InetSocketAddress;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mantono.webserver.rest.Response;
import com.mantono.webserver.rest.ResponseCode;

public class SocketListener
{
	private final int port;
	private final BlockingQueue<Socket> clientQueue;
	private final ConnectionHandler connections;
	
	protected SocketListener(int port) throws ClassNotFoundException, IOException
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
			incomingRequests.setReuseAddress(true);
			incomingRequests.bind(new InetSocketAddress(port));
			
			while(true)
			{
				Socket connection = incomingRequests.accept();
				if(!clientQueue.offer(connection, 800, TimeUnit.MILLISECONDS))
				{
					returnServerBusy(connection);
					connection.close();
				}	
			}
		}
		catch(IOException | InterruptedException e)
		{
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	
	private void returnServerBusy(Socket connection) throws IOException
	{
		final Response busy = new WebPage(ResponseCode.SERVICE_UNAVAILABLE);
		ResponseSender sender = new ResponseSender(connection, busy);
		sender.send();
		sender.close();
	}

	public static void main(String[] args) throws ClassNotFoundException, IOException
	{
		SocketListener sl = new SocketListener(8888);
		sl.listen();
	}

}
