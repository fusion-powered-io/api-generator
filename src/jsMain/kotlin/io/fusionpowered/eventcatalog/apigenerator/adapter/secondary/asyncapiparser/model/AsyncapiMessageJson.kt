package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

import io.fusionpowered.eventcatalog.common.ExtendedApi

@JsExport
external interface AsyncapiMessageJson: ExtendedApi {

  val description: String?

}