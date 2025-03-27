package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiInfo {

  fun title(): String

  fun version(): String

  fun id(): String?

  fun description(): String?

  fun tags(): AsyncapiCollection<AsyncapiTag>?

  fun externalDocs(): AsyncapiExternalDocumentation?

}