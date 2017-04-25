package com.mantono.webserver.security;

import java.io.Serializable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import com.mantono.webserver.Header;

public class Authenticator implements Serializable
{
	private static Authenticator instance;
	private final AccountManager manager;
	private final Map<Integer, List<Cookie>> cookies;
	private final boolean useCookies, useTokens;

	private Authenticator(final AccountManager manager, final boolean useCookies, final boolean useTokens)
	{
		this.manager = manager;
		this.cookies = new ConcurrentHashMap<Integer, List<Cookie>>();
		this.useCookies = useCookies;
		this.useTokens = useTokens;
	}

	public static Authenticator create(final AccountManager manager, boolean useCookies, final boolean useTokens)
	{
		if(instance != null)
			return instance;

		instance = new Authenticator(manager, useCookies, useTokens);
		return instance;
	}

	public static Authenticator create(final AccountManager manager)
	{
		return create(manager, true, true);
	}

	public boolean isAuthenticated(Header header, int user)
	{
		// TODO Auto-generated method stub
		return true;
	}
}
