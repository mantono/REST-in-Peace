package com.mantono.webserver.security;

import java.io.Serializable;

/**
 * This class in a representation of an e-mail address. Using this class
 * instead of a plain {@link String} for storing the e-mail address
 * delivers some extra syntax checking of the address, but offers no extra
 * functionality beyond that.
 * 
 * @author Anton &Ouml;sterberg
 *
 */
public class EmailAddress implements Serializable
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 2016507704857516179L;
	private final String prefix;
	private final String suffix;

	/**
	 * @param email the e-mail address as a {@link String}.
	 * @throws IllegalArgumentException if the given address is missing the \@ symbol.
	 */
	public EmailAddress(String email)
	{
		if(!(email.contains("@")))
			throw new IllegalArgumentException("The e-mail address does not contain a @ smybol");
		String[] adressDivided = email.split("@");
		int indexSuffix = adressDivided.length - 1;
		suffix = adressDivided[indexSuffix];
		String tempPrefix = "";
		for(int i = 0; i < indexSuffix; i++)
		{
			tempPrefix += adressDivided[i];
			if(i + 1 < indexSuffix)
				tempPrefix += "@";
		}
		prefix = tempPrefix;
	}

	/**
	 * This method checks for some (but not all) of the ways an e-mail
	 * address can be badly formatted.
	 * @return false if the e-mail address was not illegally formatted
	 * in some way, else true.
	 */
	public boolean isValidAddress()
	{
		final boolean doubleDotAfterAt = suffix.contains("..");
		final boolean singleDot = suffix.contains(".");
		final boolean multipleAts = prefix.contains("@");
		final boolean prefixUnEscapedCharacters = prefix.matches("[(),;:<>]");
		final boolean suffixUnEscapedCharacters = suffix.matches("[(),;:<>]");
		return !(doubleDotAfterAt || multipleAts || prefixUnEscapedCharacters || suffixUnEscapedCharacters || !singleDot);
	}

	@Override
	public String toString()
	{
		return prefix + "@" + suffix;
	}
}
