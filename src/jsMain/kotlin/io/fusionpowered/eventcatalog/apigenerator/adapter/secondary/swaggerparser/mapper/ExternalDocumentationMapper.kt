package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiExternalDocumentation
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ExternalDocumentation

fun OpenapiExternalDocumentation.toExternalDocumentation() =
  ExternalDocumentation(
    description = description ?: "",
    url = url ?: ""
  )