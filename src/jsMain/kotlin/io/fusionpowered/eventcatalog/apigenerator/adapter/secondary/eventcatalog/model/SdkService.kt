package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
class SdkService(
  val id: String,
  val name: String,
  val version: String,
  val summary: String,
  val schemaPath: String,
  val badges: Array<SdkBadge>?,
  val sends: Array<SdkSendsPointer>?,
  val receives: Array<SdkReceivesPointer>?,
  val specifications: dynamic,
  val owners: Array<String>?,
  val repository: SdkRepository?,
  val markdown: String,
  val editUrl: String
)