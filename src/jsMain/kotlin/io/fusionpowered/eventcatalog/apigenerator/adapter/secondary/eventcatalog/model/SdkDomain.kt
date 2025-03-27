package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

@JsExport
class SdkDomain(
  val id: String,
  val name: String,
  val version: String,
  val summary: String,
  val services: Array<SdkResourcePointer>?,
  val owners: Array<String>?,
  val markdown: String,
)
