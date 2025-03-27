package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkRepository
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Repository

fun SdkRepository.toRepository() =
  Repository(
    language = language,
    url = url
  )

fun Repository.toSdkRepository() =
  SdkRepository(
    language = language,
    url = url
  )

