package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiBinding {

  fun protocol(): String

  fun version(): String

}