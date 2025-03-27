package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiChannelParameter
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Parameter

fun AsyncapiChannelParameter.toParameter() =
  Parameter(
    enum = enum?.toList() ?: emptyList(),
    default = default ?: "",
    examples = examples?.toList() ?: emptyList(),
    description = description ?: ""
  )