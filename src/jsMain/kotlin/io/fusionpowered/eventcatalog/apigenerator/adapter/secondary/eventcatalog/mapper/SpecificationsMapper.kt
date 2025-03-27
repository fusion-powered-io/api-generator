package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkSpecifications
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Specifications

fun SdkSpecifications.toSpecifications() =
    Specifications(
      openapiPath = openapiPath ?: "",
      asyncapiPath = asyncapiPath ?: ""
    )

fun Specifications.toSdkSpecifications() =
  SdkSpecifications(
    openapiPath =  openapiPath,
    asyncapiPath =  asyncapiPath
  )



