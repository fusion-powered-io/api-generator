package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model

@JsExport
data class OpenapiParameter(
  val name: String,
  val `in`: String,
  val required: Boolean?,
  val description: String?,
)