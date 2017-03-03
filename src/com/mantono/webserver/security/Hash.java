package com.mantono.webserver.security;

import java.io.UnsupportedEncodingException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import javax.xml.bind.DatatypeConverter;

public final class Hash
{
	private final MessageDigest digest;
	private final String algorithm;
	private final String content;
	private final String salt;
	private byte[] bytes;
	
	public Hash(String content, String salt, String algorithm)
	throws NoSuchAlgorithmException
	{
		this.algorithm = algorithm;
		this.digest = MessageDigest.getInstance(algorithm);
		this.content = content;
		this.salt = salt;
		generate();
	}
	
	public Hash(String content, String salt)
	throws NoSuchAlgorithmException
	{
		this.algorithm = "SHA-512";
		this.digest = MessageDigest.getInstance(algorithm);
		this.content = content;
		this.salt = salt;
		generate();
	}
	
	public Hash(byte[] bytes)
	{
		this.bytes = bytes;
		this.algorithm = "unknown";
		this.digest = null;
		this.content = null;
		this.salt = null;
	}
	
	public Hash(String hexString)
	{
		this.bytes = DatatypeConverter.parseHexBinary(hexString);
		this.algorithm = "unknown";
		this.digest = null;
		this.content = null;
		this.salt = null;
	}
	
	private void generate()
	{
		digest.reset();
		try
		{
			bytes = digest.digest((content + salt).getBytes("UTF-8"));
		}
		catch(UnsupportedEncodingException e)
		{
			System.err.println("You are missing support for encoding UTF-8 and hash can therefore not be computed");
			e.printStackTrace();
		}
	}
	
	public byte[] bytes()
	{
		return bytes;
	}
	
	public String asHexString()
	{
		return DatatypeConverter.printHexBinary(bytes).toLowerCase();
	}
	
	@Override
	public String toString()
	{
		StringBuilder result = new StringBuilder();
		for(byte b : bytes)
			result.append(b + " ");
		return result.toString();
	}
	
	@Override
	public boolean equals(Object other)
	{
		if(other == null)
			return false;
		if(other.getClass() != this.getClass())
			return false;
		Hash otherHash = (Hash) other;		
		
		if(this.bytes == null || otherHash.bytes.length == 0)
			return false;
		if(this.bytes.length != otherHash.bytes.length)
			return false;
		int result = 0;
		for(int i = 0; i < this.bytes.length; i++)
			result |= this.bytes[i] ^ otherHash.bytes[i];
	    return result == 0;
	}
	
	@Override
	public int hashCode()
	{
		int i = 1;
		for(byte b : bytes)
			i += i * 11 + b;
		return i;
	}

}
