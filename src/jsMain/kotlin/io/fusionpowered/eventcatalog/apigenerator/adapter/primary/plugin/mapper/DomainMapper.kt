package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.DomainProperty
import io.fusionpowered.eventcatalog.apigenerator.model.import.DomainImportData

fun DomainProperty.toDomainImportData() =
  DomainImportData(
    id = id,
    name = name,
    version = version,
    owners = owners?.toSet() ?: emptySet()
  )