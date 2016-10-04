package com.mantono.webserver;

import java.io.IOException;

public class Server
{
	public static final int DEFAULT_PORT_NUMBER = 8888;
	public static final int PORT_LOWER_BOUND = 1024;
	public static final int PORT_UPPER_BOUND = 65536;

	public static void start(String[] args) throws ClassNotFoundException, IOException
	{
		final int port = parsePortNumber(args);

		SocketListener sl = new SocketListener(port);
		sl.listen();
	}

	private static int parsePortNumber(String[] args)
	{
		int port = DEFAULT_PORT_NUMBER;

		try
		{
			if(args.length > 0)
			{
				int argNumber = Integer.parseInt(args[0]);
				if(argNumber <= PORT_LOWER_BOUND && argNumber != 80)
				{
					System.err.println("Invalid port number " + argNumber + ", port must be 80 or above "
							+ PORT_LOWER_BOUND);
					System.exit(1);
				}
				else if(argNumber >= PORT_UPPER_BOUND)
				{
					System.err.println("Invalid port number " + argNumber + ", port must be below " + PORT_UPPER_BOUND);
					System.exit(2);
				}
				else
				{
					port = argNumber;
				}

			}
		}
		catch(NumberFormatException exception)
		{
			System.err.println("Argument " + args[0] + " is not a valid number.");
			System.exit(3);
		}
		
		return port;
	}

}
