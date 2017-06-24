package com.mantono.webserver.security

import java.time.ZonedDateTime

sealed class Cookie
{
	abstract val key: String
	abstract val value: String
	abstract val secure: Boolean
	abstract val httpOnly: Boolean

	fun asSetCookie(): String
	{
		val str: StringBuilder = StringBuilder("Set-Cookie: ")
		str.append("$key:$value; ")
		if(this is PersistentCookie)
			str.append(expires.toString() + "; ")
		if(secure)
			str.append("Secure; ")
		if(httpOnly)
			str.append("HttpOnly; ")

		str.removeRange(str.lastIndex-1, str.lastIndex)

		return str.toString()
	}
}

const val DEFAULT_COOKIE_LIFE_LENGTH: Long = 3600L

data class PersistentCookie(override val key: String,
                            override val value: String,
                            val expires: ZonedDateTime,
                            override val secure: Boolean = false,
                            override val httpOnly: Boolean = false): Cookie()
{
	constructor(key: String,
	            value: String,
	            maxAge: Long = DEFAULT_COOKIE_LIFE_LENGTH,
	            secure: Boolean = false,
	            httpOnly: Boolean = false):
			this(key, value, ZonedDateTime.now().plusSeconds(maxAge), secure, httpOnly)

	fun isExpired(): Boolean = expires.isBefore(ZonedDateTime.now())
}

data class SessionCookie(override val key: String,
                            override val value: String,
                            override val secure: Boolean = false,
                            override val httpOnly: Boolean = false): Cookie()

private fun plusSeconds(time: ZonedDateTime = ZonedDateTime.now(), seconds: Long): ZonedDateTime
{
	return time.plusSeconds(seconds)
}

class CookieJar()
{
	val cookies: MutableMap<String, PersistentCookie> = HashMap()

	fun add(key: String, value: String, expiresIn: Long = DEFAULT_COOKIE_LIFE_LENGTH): String?
	{
		val cookie = PersistentCookie(key, value, expiresIn)
		return add(cookie)?.value
	}

	fun add(cookie: PersistentCookie) = cookies.put(cookie.key, cookie)
	operator fun get(key: String): String? = cookies[key]?.value
	operator fun contains(key: String): Boolean = cookies.containsKey(key)
	fun remove(key: String): Boolean = cookies.remove(key) != null

	fun removeOld(): Set<PersistentCookie>
	{
		return cookies.values.asSequence()
				.filter { it.isExpired() }
				.map{ discard(it, cookies) }
				.filterNotNull()
				.toSet()
	}

	private fun discard(it: PersistentCookie, cookies: MutableMap<String, PersistentCookie>): PersistentCookie?
	{
		if(cookies.remove(it.key, it))
			return it
		return null
	}
}