package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model

import io.fusionpowered.eventcatalog.common.mapOf

@JsExport
data class OpenapiBody(
  val description: String?
)

val OpenapiBody.mediaTypeMap: Map<String, String>
  get() =
    mapOf<String, String>(asDynamic().content).values.firstOrNull()
      ?.let { body -> mapOf(body) }
      ?: emptyMap()