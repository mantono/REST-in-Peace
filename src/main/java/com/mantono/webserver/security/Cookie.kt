package com.mantono.webserver.security

import java.time.ZonedDateTime

class Cookie(val key: String,
             val value: String,
             val expires: ZonedDateTime = ZonedDateTime.parse(),
             val secure: Boolean = false,
             val httpOnly: Boolean = false)
{
}