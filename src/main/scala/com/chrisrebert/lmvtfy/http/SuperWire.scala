package com.chrisrebert.lmvtfy.http

import java.util.{Collection=>JavaCollection}
import java.util.Map.{Entry=>MapEntry}
import java.io.InputStream
import com.jcabi.github.wire.RetryCarefulWire
import com.jcabi.http.{Wire,Request,Response}

case class SuperWire(private val wire: Wire, userAgent: UserAgent, threshold: Int) extends Wire {
  private val wrappedWire = UserAgentWire(wire = new RetryCarefulWire(wire, threshold), userAgent = userAgent)

  @Override
  def send(
    request: Request,
    home: String,
    method: String,
    headers: JavaCollection[MapEntry[String, String]],
    content: InputStream
  ): Response = wrappedWire.send(request, home, method, headers, content)
}
