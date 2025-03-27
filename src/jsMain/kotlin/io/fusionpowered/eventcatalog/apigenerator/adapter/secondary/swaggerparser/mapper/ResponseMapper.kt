package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiBody
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData.Message.Response

fun Map.Entry<String, OpenapiBody>.toResponse() =
  Response(
    status = key,
    body = value.toBody()
  )