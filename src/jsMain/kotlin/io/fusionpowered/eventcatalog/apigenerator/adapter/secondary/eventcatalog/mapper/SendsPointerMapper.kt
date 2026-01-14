package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkSendsPointer
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.SendsPointer

fun SdkSendsPointer.toSendsPointer() =
  SendsPointer(
    id = id,
    version = version,
    to = to?.map { it.toResourcePointer() }?.toMutableList() ?: mutableListOf()
  )

fun SendsPointer.toSdkSendsPointer() =
  SdkSendsPointer(
    id = id,
    version = version,
    to = to.map { it.toSdkResourcePointer() }.toTypedArray()
  )