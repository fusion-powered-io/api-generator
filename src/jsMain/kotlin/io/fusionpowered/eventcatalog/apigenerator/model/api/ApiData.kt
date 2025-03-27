package io.fusionpowered.eventcatalog.apigenerator.model.api


import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ExternalDocumentation

sealed interface ApiData {

  val service: Service

  sealed interface Service {
    val name: String
    val version: String
    val schemaPath: String
    val description: String
    val tags: List<String>
    val externalDocumentation: ExternalDocumentation?
  }

  sealed interface Message {
    val id: String
    val name: String
    val version: String
    val summary: String
    val description: String
    val type: Type
    val direction: Direction
    val externalDocumentation: ExternalDocumentation?
    val tags: List<String>
    val role: Role

    enum class Type {
      Event,
      Command,
      Query
    }

    enum class Direction {
      Sends,
      Receives
    }

    enum class Role {
      Provided,
      Consumed
    }

  }

}