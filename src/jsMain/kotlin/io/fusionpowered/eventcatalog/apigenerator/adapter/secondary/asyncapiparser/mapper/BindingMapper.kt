package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiBinding
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData

fun AsyncapiBinding.toBinding() =
  AsyncapiData.Channel.Binding(
    protocol = protocol()
  )