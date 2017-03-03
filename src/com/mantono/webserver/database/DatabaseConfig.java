package com.mantono.webserver.database;

import java.util.HashSet;
import java.util.Set;

import com.mantono.argumentloader.ProgramOption;

public enum DatabaseConfig implements ProgramOption
{
	CONFIG('c', "config", "Set the location of the config file.", "./.config"),
	PORT('d', "port", "Set the port number for the MySQL database.", "3306"),
	ADDRESS('e', "address", "Address to the database server.", "127.0.0.1"),
	USERNAME('u', "user", "Set the username to the MySQL database.", ""),
	PASSWORD('x', "password", "Set the password for the user on the MySQL database.", ""),
	DATABASE('e', "database", "Set which database that will be used on the MySQL server", ""),
	LIMIT('l', "limit", "Maximum amount of simultaneous connections allowed", "50"),
	HELP('h', "help", "Displays these options.", "");

	private final char shortFlag;
	private final String longFlag, description, defaultValue;
	private final Set<Character> usedChars = new HashSet<Character>(16);

	/**
	 * Sets the short flag, the long flag, its description and its default value.
	 * @param shortFlag the character to be used for the short flag.
	 * @param longFlag the String to be used for the long flag.
	 * @param description a description of this setting.
	 * @param defaultValue this settings default value.
	 */
	private DatabaseConfig(final char shortFlag, final String longFlag, final String description, final String defaultValue)
	{
		if(usedChars.contains(shortFlag))
			throw new IllegalArgumentException("The character '" + shortFlag + "' is already used by another flag.");
		usedChars.add(shortFlag);
		this.shortFlag = shortFlag;
		this.longFlag = longFlag;
		this.description = description;
		this.defaultValue = defaultValue;
	}

	@Override
	public char getShortFlag()
	{
		return shortFlag;
	}

	@Override
	public String getLongFlag()
	{
		return longFlag;
	}

	@Override
	public String getDescription()
	{
		return description;
	}

	@Override
	public String defaultValue()
	{
		return defaultValue;
	}

	@Override
	public String toString()
	{
		return helpDescription();
	}

	@Override
	public boolean takesArgument()
	{
		return true;
	}
}