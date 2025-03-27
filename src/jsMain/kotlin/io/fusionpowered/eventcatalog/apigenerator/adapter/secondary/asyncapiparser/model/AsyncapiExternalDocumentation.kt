package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiExternalDocumentation {

  val url: String?
  val description: String?

  fun url(): String?
  fun description(): String?

}