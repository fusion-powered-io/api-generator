package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.ServiceProperty
import io.fusionpowered.eventcatalog.apigenerator.model.import.ServiceImportData

fun ServiceProperty.toServiceImportData() =
  ServiceImportData(
    id = id,
    name = name,
    openapiPath = openapiPath,
    asyncapiPath = asyncapiPath,
    owners = owners?.toSet() ?: emptySet()
  )