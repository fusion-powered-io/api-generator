package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkService
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkSpecifications
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Repository
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Service
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Specifications
import io.fusionpowered.eventcatalog.common.asDynamicWithNoEmptyProperties

fun SdkService.toService() =
  Service(
    id = id,
    name = name,
    version = version,
    summary = summary,
    schemaPath = schemaPath,
    badges = badges?.map { it.toBadge() }?.toSet() ?: emptySet(),
    owners = owners?.toSet() ?: emptySet(),
    markdown = markdown,
    repository = repository?.toRepository() ?: Repository(),
    sends = sends?.map { it.toResourcePointer() }?.toMutableList() ?: mutableListOf(),
    receives = receives?.map { it.toResourcePointer() }?.toMutableList() ?: mutableListOf(),
    specifications = specifications.unsafeCast<SdkSpecifications?>()?.toSpecifications() ?: Specifications(),
  )

fun Service.toSdkService() =
  SdkService(
    id = id,
    name = name,
    version = version,
    summary = summary,
    schemaPath = schemaPath,
    badges = badges.map { it.toSdkBadge() }.toTypedArray(),
    owners = owners.toTypedArray(),
    markdown = markdown,
    repository = repository.toSdkRepository(),
    sends = sends.map { it.toSdkResourcePointer() }.toTypedArray(),
    receives = receives.map { it.toSdkResourcePointer() }.toTypedArray(),
    specifications = specifications.toSdkSpecifications().asDynamicWithNoEmptyProperties()
  )

