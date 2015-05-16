package com.chrisrebert.lmvtfy.http

import java.util.{Collection=>JavaCollection}
import java.util.Map.{Entry=>MapEntry}
import java.io.InputStream
import scala.collection.JavaConverters._
import com.jcabi.http._

object UserAgentWire {
  private val userAgentHeader = "User-Agent"
}
case class UserAgentWire(private val wire: Wire, userAgent: UserAgent) extends Wire {
  @Override
  def send(
    request: Request,
    home: String,
    method: String,
    headers: JavaCollection[MapEntry[String, String]],
    content: InputStream
  ): Response = {
    val header = new ImmutableHeader(UserAgentWire.userAgentHeader, userAgent.userAgent)
    val newHeaders = header +: headers.asScala.filter{ _.getKey != UserAgentWire.userAgentHeader}.toSeq
    wire.send(request, home, method, newHeaders.asJava, content)
  }
}
