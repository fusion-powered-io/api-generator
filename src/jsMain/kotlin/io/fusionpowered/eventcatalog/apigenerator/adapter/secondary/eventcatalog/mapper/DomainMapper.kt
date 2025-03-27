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
    markdown = markdown,
    owners = owners?.toSet() ?: emptySet(),
  )

fun Domain.toSdkDomain() =
  SdkDomain(
    id = id,
    name = name,
    version = version,
    summary = summary,
    services = services
      .map { it.toSdkResourcePointer() }.toTypedArray(),
    markdown = markdown,
    owners = owners.toTypedArray()
  )