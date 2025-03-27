package io.fusionpowered.eventcatalog.apigenerator.model.catalog

import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData

data class Channel(
  val id: String,
  val name: String,
  val version: String,
  val summary: String = "",
  val address: String = "",
  val protocols: Set<String> = emptySet(),
  val parameters: Map<String, Parameter> = emptyMap(),
  val markdown: String = "",
) {

  constructor(channelData: AsyncapiData.Channel) : this(
    id = channelData.id,
    name = channelData.name,
    version = channelData.version,
    summary = channelData.summary,
    address = channelData.address,
    protocols = channelData.bindings.map { it.protocol }.toSet(),
    parameters = channelData.parameters,
    markdown = channelData.defaultMarkdown,
  )

  companion object {

    private val AsyncapiData.Channel.defaultMarkdown: String
      get() {
        val overviewMarkdown = when {
          description.isNotBlank() -> "## Overview\n$description\n"
          else -> ""
        }

        val externalDocumentationMarkdown = externalDocumentation
          ?.run { "## External documentation\n-[${description}](${url})\n" }
          ?: ""

        return overviewMarkdown +
          "<ChannelInformation />\n" +
          externalDocumentationMarkdown
      }

  }


}
