package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiChannel
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiInfo
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.parameters
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.common.extensions

fun AsyncapiChannel.toChannelData(info: AsyncapiInfo) =
  AsyncapiData.Channel(
    id = id(),
    name = json().title ?: id(),
    version = json().extensions["x-eventcatalog-channel-version"] ?: info.version(),
    summary = json().summary ?: "",
    description = description() ?: "",
    address = address() ?: "",
    bindings = bindings()?.all()?.map { it.toBinding() } ?: emptyList(),
    parameters = json().parameters.mapValues { it.value.toParameter() },
    externalDocumentation = json().externalDocs?.toExternalDocumentation()
  )