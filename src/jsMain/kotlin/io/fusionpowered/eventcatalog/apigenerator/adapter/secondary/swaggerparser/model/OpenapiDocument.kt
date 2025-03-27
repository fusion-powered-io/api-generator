package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model

import io.fusionpowered.eventcatalog.common.mapOf

@JsExport
class OpenapiDocument(
  val tags: Array<OpenapiTag>?,
  val info: OpenapiInfo,
  val externalDocs: OpenapiExternalDocumentation?
)

val OpenapiDocument.pathMap: Map<String, OpenapiMethodToOperationMapReference>
  get() = mapOf(asDynamic().paths)