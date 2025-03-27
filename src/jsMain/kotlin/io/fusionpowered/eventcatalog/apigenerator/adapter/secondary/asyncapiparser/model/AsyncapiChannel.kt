package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiChannel {

  fun id(): String

  fun json(): AsyncapiChannelJson

  fun description(): String?

  fun address(): String?

  fun bindings(): AsyncapiCollection<AsyncapiBinding>?

  fun messages(): AsyncapiCollection<AsyncapiMessage>

}