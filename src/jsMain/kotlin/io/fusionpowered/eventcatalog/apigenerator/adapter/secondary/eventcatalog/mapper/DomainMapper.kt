package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkDomain
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain

fun SdkDomain.toDomain() =
  Domain(
    id = id,
    name = name,
    version = version,
    summary = summary,
    services = services?.map { it.toResourcePointer() }?.toMutableList() ?: mutableListOf(),
    entities = entities?.toSet() ?: emptySet(),
    owners = owners?.toSet() ?: emptySet(),
    markdown = markdown,
    editUrl = editUrl,
  )

fun Domain.toSdkDomain() =
  SdkDomain(
    id = id,
    name = name,
    version = version,
    summary = summary,
    services = services
      .map { it.toSdkResourcePointer() }.toTypedArray(),
    entities = entities.toTypedArray(),
    owners = owners.toTypedArray(),
    markdown = markdown,
    editUrl = editUrl,
  )