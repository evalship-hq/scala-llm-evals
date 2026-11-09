package com.example.support

import com.anthropic.client.okhttp.AnthropicOkHttpClient
import com.anthropic.models.messages.{MessageCreateParams, OutputConfig}
import java.nio.file.{Files, Path}
import scala.jdk.CollectionConverters.*
import scala.jdk.OptionConverters.*

/** A support bot for a web shop. Build it once: it holds the HTTP client. */
class Support:
  private val client = AnthropicOkHttpClient.fromEnv() // reads ANTHROPIC_API_KEY
  private val systemPrompt = Files.readString(Path.of("prompts/support.md"))

  def answer(message: String): String =
    val params = MessageCreateParams.builder()
      .model("claude-opus-5-5")
      .maxTokens(1024L)
      // Opus 5.5 always thinks; low effort is enough for a short reply.
      .outputConfig(OutputConfig.builder().effort(OutputConfig.Effort.LOW).build())
      .system(systemPrompt)
      .addUserMessage(message)
      .build()
    client.messages().create(params).content().asScala
      .flatMap(_.text().toScala)
      .map(_.text())
      .mkString
