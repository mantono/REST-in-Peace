package com.mantono.webserver.security;

public interface AccountManager
{
	boolean createAccount(String username, EmailAddress address, String password, String repeatedPassword);
	boolean emailAddressExists(EmailAddress address);
	boolean userExists(String username);
	boolean permitLogin(String username, String password);
	CharSequence getUserToken(String username);
}
