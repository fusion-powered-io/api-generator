package io.fusionpowered.eventcatalog.apigenerator.model.import

data class DomainImportData(
  val id: String,
  val name: String,
  val version: String,
  val owners: Set<String>,
)