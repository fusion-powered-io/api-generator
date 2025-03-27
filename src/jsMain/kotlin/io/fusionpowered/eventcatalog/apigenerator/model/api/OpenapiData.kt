package io.fusionpowered.eventcatalog.apigenerator.model.api

import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role.Provided
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ExternalDocumentation

data class OpenapiData(
  override val service: Service,
  val messages: List<Message>
) : ApiData {

  data class Service(
    override val name: String,
    override val version: String,
    override val schemaPath: String,
    override val description: String,
    override val tags: List<String>,
    override val externalDocumentation: ExternalDocumentation?
  ) : ApiData.Service

  data class Message(
    override val id: String,
    override val name: String,
    override val version: String,
    override val summary: String,
    override val description: String,
    override val type: Message.Type,
    override val direction: Message.Direction,
    override val externalDocumentation: ExternalDocumentation?,
    override val tags: List<String>,
    override val role: Message.Role = Provided,
    val path: String,
    val method: String,
    val request: Request,
    val responses: Map<String, Response>
  ) : ApiData.Message {

    data class Request(
      val parameters: List<Parameter> = emptyList(),
      val body: Body? = null,
    ) {

      data class Body(
        val description: String,
        val content: Content? = null
      ) {

        sealed interface Content {

          val value: String

          class Schema(override val value: String) : Content

          class Other(override val value: String) : Content

        }

      }

      data class Parameter(
        val name: String,
        val location: String,
        val required: Boolean,
        val description: String,
      )

    }

    data class Response(
      val status: String,
      val body: Request.Body
    )

  }

}