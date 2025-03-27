package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiOperation {

  fun json(): AsyncapiOperationJson

  fun action(): String?

  fun messages(): AsyncapiCollection<AsyncapiMessage>

}