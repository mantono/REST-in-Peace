package com.mantono.webserver;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.PrintStream;
import java.net.Socket;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.TimeUnit;

import com.mantono.webserver.rest.ResponseCode;

public class RequestResponder implements Runnable
{
	private final BlockingQueue<Socket> incoming;
	
	public RequestResponder(final BlockingQueue<Socket> requests)
	{
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
				
				InputStreamReader socketReader = new InputStreamReader(socket.getInputStream());
				BufferedReader bufferedReader = new BufferedReader(socketReader);
				
				
				String line;
				do
				{
					line = bufferedReader.readLine();
					if(line == null)
						break;
					System.out.println(line);
				}while(line.length() != 0);
				
				OutputStream socketOut = socket.getOutputStream();
				PrintStream streamOut = new PrintStream(socketOut, true);
				streamOut.append(ResponseCode.OK.toString());
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
		}
	}

}
