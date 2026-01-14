package io.fusionpowered.eventcatalog.apigenerator.model.catalog

data class SendsPointer(
  val id: String,
  val version: String,
  val to: MutableList<ResourcePointer> = mutableListOf(),
)