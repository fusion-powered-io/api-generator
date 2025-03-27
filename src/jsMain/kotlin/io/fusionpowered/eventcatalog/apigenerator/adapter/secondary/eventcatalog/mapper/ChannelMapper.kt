package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkChannel
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkParameter
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Channel
import io.fusionpowered.eventcatalog.common.mapOf
import io.fusionpowered.eventcatalog.common.toRecord

fun SdkChannel.toChannel() =
  Channel(
    id = id,
    name = name,
    version = version,
    summary = summary ?: "",
    address = address ?: "",
    protocols = protocols?.toSet() ?: emptySet(),
    parameters = mapOf<String, SdkParameter>(parameters).mapValues { it.value.toParameter() },
    markdown = markdown,
  )

fun Channel.toSdkChannel() =
  SdkChannel(
    id = id,
    name = name,
    version = version,
    summary = summary,
    address = address,
    protocols = protocols.toTypedArray(),
    parameters = parameters.mapValues { it.value.toSdkParameter() }.toRecord(),
    markdown = markdown,
  )
