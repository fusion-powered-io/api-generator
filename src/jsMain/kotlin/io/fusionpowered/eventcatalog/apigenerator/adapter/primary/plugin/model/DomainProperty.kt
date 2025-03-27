package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model

@JsExport
class DomainProperty(
  val id: String,
  val name: String,
  val version: String,
  val owners: Array<String>? = null,
)