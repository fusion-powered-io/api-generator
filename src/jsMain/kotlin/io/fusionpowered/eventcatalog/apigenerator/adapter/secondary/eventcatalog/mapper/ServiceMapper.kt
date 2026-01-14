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
    sends = sends?.map { it.toSendsPointer() }?.toMutableList() ?: mutableListOf(),
    receives = receives?.map { it.toReceivesPointer() }?.toMutableList() ?: mutableListOf(),
    specifications = specifications.unsafeCast<SdkSpecifications?>()?.toSpecifications() ?: Specifications(),
    editUrl = editUrl,
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
    sends = sends.map { it.toSdkSendsPointer() }.toTypedArray(),
    receives = receives.map { it.toSdkReceivesPointer() }.toTypedArray(),
    specifications = specifications.toSdkSpecifications().asDynamicWithNoEmptyProperties(),
    editUrl = editUrl,
  )

