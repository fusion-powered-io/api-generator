package io.fusionpowered.eventcatalog.apigenerator.model.api

import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ExternalDocumentation
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Parameter

data class AsyncapiData(
  override val service: Service,
  val channels: List<Channel>,
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

  data class Channel(
    val id: String,
    val name: String,
    val version: String,
    val summary: String,
    val description: String,
    val address: String,
    val bindings: List<Binding>,
    val parameters: Map<String, Parameter>,
    val externalDocumentation: ExternalDocumentation?
  ) {

    data class Binding(
      val protocol: String
    )

  }

  data class Message(
    override val id: String,
    override val name: String,
    override val version: String,
    override val summary: String,
    override val description: String,
    override val type: ApiData.Message.Type,
    override val direction: ApiData.Message.Direction,
    override val externalDocumentation: ExternalDocumentation?,
    override val tags: List<String>,
    override val role: Role,
    val payload: Payload?,
    val channels: List<Channel>
  ) : ApiData.Message {

    data class Payload(
      val schema: String,
      val format: Format
    ) {

      enum class Format(val extension: String) {
        Avro("avsc"),
        Yaml("yaml"),
        Json("json"),
        Protobuf("proto"),
      }

    }

  }

}