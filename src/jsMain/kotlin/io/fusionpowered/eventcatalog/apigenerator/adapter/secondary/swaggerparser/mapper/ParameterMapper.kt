package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiParameter
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData.Message.Request.Parameter

fun OpenapiParameter.toParameter() =
  Parameter(
    name = name,
    location = `in`,
    required = required ?: false,
    description = description ?: "",
  )