package io.fusionpowered.eventcatalog.apigenerator.model.catalog

data class Parameter(
  val enum: List<String>,
  val default: String,
  val examples: List<String>,
  val description: String
)
