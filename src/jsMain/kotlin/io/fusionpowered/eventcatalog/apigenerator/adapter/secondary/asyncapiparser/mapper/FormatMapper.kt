package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData.Message.Payload.Format
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData.Message.Payload.Format.Avro

fun String.toFormat() =
  when {
    contains("avro") -> Avro
    contains("yml") || contains("yaml") -> Format.Yaml
    contains("protobuf") -> Format.Protobuf
    else -> Format.Json
  }
