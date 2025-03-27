package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model


@JsExport
external interface AsyncapiDocument {

  fun version(): String

  fun info(): AsyncapiInfo

  fun allChannels(): AsyncapiCollection<AsyncapiChannel>

  fun allOperations(): AsyncapiCollection<AsyncapiOperation>

}
