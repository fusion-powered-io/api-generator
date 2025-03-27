package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model

import io.fusionpowered.eventcatalog.common.ExtendedApi
import io.fusionpowered.eventcatalog.common.mapOf

@JsExport
external interface OpenapiOperation: ExtendedApi {

  val summary: String?
  val operationId: String?
  val description: String?
  val externalDocs: OpenapiExternalDocumentation?
  val tags: Array<String>?
  val parameters: Array<OpenapiParameter>?
  @JsName("requestBody")
  val request: OpenapiBody?

}

val OpenapiOperation.responses: Map<String, OpenapiBody>
  get() =
    mapOf(asDynamic().responses)