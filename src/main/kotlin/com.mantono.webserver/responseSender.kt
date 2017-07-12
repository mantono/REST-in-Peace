package com.mantono.webserver

import com.mantono.webserver.rest.HeaderField
import com.mantono.webserver.rest.Response
import com.mantono.webserver.rest.ResponseCode
import java.io.PrintStream
import java.net.Socket

private const val EOL: String = "\r\n"

fun send(socket: Socket, response: Response): Int
{
	val socketOut = socket.getOutputStream()
	val streamOut = PrintStream(socketOut, true)
	printResponse(streamOut, response)
	streamOut.flush()
	socketOut.flush()
	val size = response.header[HeaderField.CONTENT_LENGTH]
	return Integer.parseInt(size ?: "-1")
}

private fun printResponse(streamOut: PrintStream, response: Response)
{
	streamOut.append(parseResponseCode(response.responseCode))
	streamOut.append(parseHeader(response.header))
	streamOut.append(response.body.toString())
	streamOut.append(EOL)
}

private fun parseResponseCode(responseCode: ResponseCode): CharSequence
{
	val responseCodeData = StringBuilder()
	responseCodeData.append("HTTP/1.1 ")
	responseCodeData.append("" + responseCode.code)
	responseCodeData.append(" " + responseCode.description)
	responseCodeData.append(EOL)
	return responseCodeData
}

private fun parseHeader(header: ResponseHeader): CharSequence
{
	val headerData = StringBuilder()
	for ((field, value) in header.fields)
	{
		headerData.append(field.getName() + ": " + value + EOL)
	}
	headerData.append(EOL + EOL)
	return headerData
}