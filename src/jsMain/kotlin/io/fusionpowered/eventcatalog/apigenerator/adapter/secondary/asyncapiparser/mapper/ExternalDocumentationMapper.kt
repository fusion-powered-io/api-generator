package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiExternalDocumentation
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ExternalDocumentation

fun AsyncapiExternalDocumentation.toExternalDocumentation() =
  ExternalDocumentation(
    description = description() ?: description ?: "",
    url = url() ?: url ?: ""
  )