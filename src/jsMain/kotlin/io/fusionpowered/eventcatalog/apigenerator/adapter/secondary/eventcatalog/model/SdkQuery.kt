package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

@JsExport
class SdkQuery(
  val id: String,
  val name: String,
  val version: String,
  val summary: String,
  val schemaPath: String,
  val badges: Array<SdkBadge>?,
  val sidebar: SdkSidebar?,
  val owners: Array<String>?,
  val markdown: String,
  val editUrl: String
)
