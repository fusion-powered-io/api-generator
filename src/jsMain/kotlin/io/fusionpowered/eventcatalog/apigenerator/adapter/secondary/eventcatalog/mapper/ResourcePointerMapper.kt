package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkResourcePointer
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ResourcePointer

fun SdkResourcePointer.toResourcePointer() =
  ResourcePointer(
    id = id,
    version = version
  )

fun ResourcePointer.toSdkResourcePointer() =
  SdkResourcePointer(
    id = id,
    version = version
  )