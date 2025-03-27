package io.fusionpowered.eventcatalog.apigenerator.model.catalog


import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData.Message.Payload.Format.Json
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData

data class Message(
  val id: String,
  val name: String,
  val version: String,
  val summary: String = "",
  val schemaPath: String = "",
  val channels: Set<ResourcePointer> = emptySet(),
  val sidebar: Sidebar = Sidebar(),
  val badges: Set<Badge> = emptySet(),
  val owners: Set<String> = emptySet(),
  val markdown: String = "",
) {

  constructor(messageApiData: ApiData.Message) : this(
    id = messageApiData.id,
    name = messageApiData.name,
    version = messageApiData.version,
    summary = messageApiData.run { summary.ifBlank { description.truncate(150) } },
    schemaPath = when (messageApiData) {
      is OpenapiData.Message -> messageApiData.request.body?.let { "request-body.json" } ?: ""
      is AsyncapiData.Message -> messageApiData.payload?.run { "schema.${format.extension}" } ?: ""
    },
    channels = when (messageApiData) {
      is OpenapiData.Message -> emptySet()
      is AsyncapiData.Message -> messageApiData.channels.map { ResourcePointer(it.id, it.version) }.toSet()
    },
    sidebar = when (messageApiData) {
      is OpenapiData.Message -> Sidebar(badge = messageApiData.method.uppercase())
      is AsyncapiData.Message -> Sidebar()
    },
    badges = when (messageApiData) {
      is OpenapiData.Message ->
        setOf(messageApiData.method.uppercase().toBadge()) +
          messageApiData.tags.map { "tag:$it".toBadge() }

      is AsyncapiData.Message -> messageApiData.tags.map { it.toBadge() }.toSet()
    },
    markdown = messageApiData.defaultMarkdown
  )

  companion object {

    private fun String.truncate(length: Int) =
      if (length >= this.length) this else substring(0, length - 3) + "..."

    private fun String.toBadge() =
      Badge(
        content = this,
        textColor = "blue",
        backgroundColor = "blue"
      )

    private val ApiData.Message.defaultMarkdown: String
      get() {
        val overviewMarkdown = when {
          description.isNotBlank() -> "## Overview\n$description\n\n"
          else -> ""

        }

        val messageMarkdown = when (this) {
          is OpenapiData.Message -> openapiMessageMarkdown
          is AsyncapiData.Message -> asyncapiMessageMarkdown
        }

        val externalDocumentationMarkdown = externalDocumentation
          ?.run { "## External documentation\n-[${description}](${url})\n" }
          ?: ""

        return overviewMarkdown +
          "## Architecture\n\n<NodeGraph />\n" +
          messageMarkdown +
          externalDocumentationMarkdown
      }

    private val OpenapiData.Message.openapiMessageMarkdown: String
      get() {
        val requestBodyMarkdown = request.body?.content
          ?.run { "### Body\n<SchemaViewer file=\"request-body.json\" maxHeight=\"500\" id=\"request-body\" />\n\n" }
          ?: ""

        return "## ${method.uppercase()}(${path})\n\n" +
          request.parameters.defaultMarkdown +
          requestBodyMarkdown +
          responses.defaultMarkdown
      }

    private val AsyncapiData.Message.asyncapiMessageMarkdown: String
      get() {
        return when {
          payload != null && payload.format == Json -> "## Schema\n<SchemaViewer file=\"schema.${payload.format.extension}\" maxHeight=\"500\" id=\"schema\" />\n\n"
          payload != null && payload.format != Json -> "## Schema\n<Schema file=\"schema.${payload.format.extension}\" title=\"Message Schema\" maxHeight=\"500\" />\n\n"
          else -> ""
        }
      }

    private val List<OpenapiData.Message.Request.Parameter>.defaultMarkdown: String
      get() {
        val headerMarkdown = "### Parameters\n"

        val parametersMarkdown = map { parameter ->
          "- **${parameter.name}** (${parameter.location})${if (parameter.required) " (required)" else ""}: ${parameter.description}\n"
        }

        return when {
          parametersMarkdown.isNotEmpty() -> headerMarkdown + parametersMarkdown.filter { it.isNotBlank() }.joinToString("\n") + "\n"
          else -> ""
        }
      }

    private val Map<String, OpenapiData.Message.Response>.defaultMarkdown: String
      get() {
        val headerMarkdown = "### Responses\n\n"

        val responsesMarkdown = entries.map { (statusCode, response) ->
          val statusColor = when (statusCode[0]) {
            '2' -> "green"
            '4' -> "orange"
            '5' -> "red"
            else -> "gray"
          }

          val statusText = when (statusCode) {
            "200" -> " OK"
            "201" -> " Created"
            "202" -> " Accepted"
            "204" -> " No Content"
            "301" -> " Moved Permanently"
            "302" -> " Found"
            "304" -> " Not Modified"
            "400" -> " Bad Request"
            "401" -> " Unauthorized"
            "403" -> " Forbidden"
            "404" -> " Not Found"
            "405" -> " Method Not Allowed"
            "409" -> " Conflict"
            "422" -> " Unprocessable Entity"
            "429" -> " Too Many Requests"
            "500" -> " Internal Server Error"
            "502" -> " Bad Gateway"
            "503" -> " Service Unavailable"
            "504" -> " Gateway Timeout"
            else -> ""
          }

          val statusMarkdown = "#### <span className=\"text-${statusColor}-500\">${statusCode}${statusText}</span>\n"
          val bodyMarkdown = response.body.content
            ?.run { "<SchemaViewer file=\"response-${statusCode}.json\" maxHeight=\"500\" id=\"response-${statusCode}\" />\n" }
            ?: ""
          statusMarkdown + bodyMarkdown
        }

        return when {
          responsesMarkdown.isNotEmpty() -> headerMarkdown + responsesMarkdown.filter { it.isNotBlank() }.joinToString("\n") + "\n"
          else -> ""
        }
      }
  }

}


