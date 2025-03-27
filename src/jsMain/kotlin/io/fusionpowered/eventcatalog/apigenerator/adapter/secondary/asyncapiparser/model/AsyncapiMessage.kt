package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiMessage {

  fun json(): AsyncapiMessageJson

  fun id(): String

  fun schemaFormat(): String?

  fun title(): String?

  fun summary(): String?

  fun description(): String?

  fun payload(): AsyncapiPayload?

  fun channels(): AsyncapiCollection<AsyncapiChannel>

  fun tags(): AsyncapiCollection<AsyncapiTag>?

  fun externalDocs(): AsyncapiExternalDocumentation?

}