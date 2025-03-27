package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model

import io.fusionpowered.eventcatalog.common.mapOf

@JsExport
interface OpenapiMethodToOperationMapReference

fun OpenapiMethodToOperationMapReference.toMap() =
  mapOf<String, OpenapiOperation>(this)