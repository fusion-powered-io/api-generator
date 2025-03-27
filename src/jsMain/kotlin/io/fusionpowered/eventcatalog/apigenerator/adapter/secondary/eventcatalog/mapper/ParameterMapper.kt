package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkParameter
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Parameter

fun SdkParameter.toParameter() =
  Parameter(
    enum = enum?.toList() ?: emptyList(),
    default = default ?: "",
    examples = examples?.toList() ?: emptyList(),
    description = description ?: ""
  )

fun Parameter.toSdkParameter() =
  SdkParameter(
    enum = enum.toTypedArray(),
    default = default,
    examples = examples.toTypedArray(),
    description = description
  )
