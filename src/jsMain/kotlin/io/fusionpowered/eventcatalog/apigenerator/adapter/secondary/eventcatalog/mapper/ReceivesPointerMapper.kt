package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkReceivesPointer
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ReceivesPointer

fun SdkReceivesPointer.toReceivesPointer() =
  ReceivesPointer(
    id = id,
    version = version,
    from = from?.map { it.toResourcePointer() }?.toMutableList() ?: mutableListOf()
  )

fun ReceivesPointer.toSdkReceivesPointer() =
  SdkReceivesPointer(
    id = id,
    version = version,
    from = from.map { it.toSdkResourcePointer() }.toTypedArray()
  )
