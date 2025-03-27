package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiBody
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.mediaTypeMap
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData.Message.Request.Body
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData.Message.Request.Body.Content
import io.fusionpowered.eventcatalog.common.circularStringify

fun OpenapiBody.toBody() =
  Body(
    description = description ?: "",
    content = when {
      mediaTypeMap.isEmpty() -> null
      mediaTypeMap.containsKey("schema") -> Content.Schema(circularStringify(mediaTypeMap["schema"]))
      else -> Content.Other(circularStringify(this))
    }
  )
