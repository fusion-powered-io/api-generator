package io.fusionpowered.eventcatalog.apigenerator.model.catalog

data class ReceivesPointer(
  val id: String,
  val version: String,
  val from: MutableList<ResourcePointer> = mutableListOf(),
)