package com.example.support

import com.anthropic.errors.{AnthropicIoException, AnthropicServiceException}
import java.io.{BufferedInputStream, ByteArrayOutputStream}
import java.nio.charset.StandardCharsets.UTF_8

/** tinyeval's worker: it reads each input up to a NUL byte on stdin, and prints the reply and a NUL byte.
  * The JVM and the client start once per worker, not once per test case. Keep logs off stdout.
  */
@main def evalWorker(): Unit =
  val support = Support()
  val stdin = BufferedInputStream(System.in)
  val input = ByteArrayOutputStream()
  Iterator.continually(stdin.read()).takeWhile(_ != -1).foreach {
    case 0 =>
      System.out.write(reply(support, input.toString(UTF_8)).getBytes(UTF_8))
      System.out.write(0)
      System.out.flush()
      input.reset()
    case b => input.write(b)
  }

// When the cause isn't the app (no key, a rate limit, a server error, a timeout or the network),
// tinyeval counts the run as couldn't run. Any other API error, like a bad model name, is the reply, so the eval fails.
def reply(support: Support, input: String): String =
  try support.answer(input)
  catch
    case e: AnthropicServiceException =>
      val status = e.statusCode()
      val reason = s"anthropic: $status ${e.`type`().map(_.asString()).orElse("")}".trim
      if status == 401 || status == 408 || status == 429 || status >= 500 then s"#tinyeval couldnt_run $reason\n"
      else reason
    case e: AnthropicIoException =>
      // the cause says "timeout" or "Failed to connect"
      s"#tinyeval couldnt_run ${Option(e.getCause).map(_.getMessage).getOrElse(e.getMessage)}\n"
