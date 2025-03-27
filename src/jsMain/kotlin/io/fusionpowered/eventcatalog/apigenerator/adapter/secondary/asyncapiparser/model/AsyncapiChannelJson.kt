package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

import io.fusionpowered.eventcatalog.common.ExtendedApi
import io.fusionpowered.eventcatalog.common.mapOf

@JsExport
class AsyncapiChannelJson : ExtendedApi {

  val title: String? = null

  val summary: String? = null

  val externalDocs: AsyncapiExternalDocumentation? = null

}

val AsyncapiChannelJson.parameters: Map<String, AsyncapiChannelParameter>
  get() =
    mapOf(asDynamic().parameters)