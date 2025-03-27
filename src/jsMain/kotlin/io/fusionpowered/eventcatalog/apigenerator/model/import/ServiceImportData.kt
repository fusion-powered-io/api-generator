package io.fusionpowered.eventcatalog.apigenerator.model.import

data class ServiceImportData(
  val id: String,
  val name: String?,
  val openapiPath: String?,
  val asyncapiPath: String?,
  val owners: Set<String>,
)