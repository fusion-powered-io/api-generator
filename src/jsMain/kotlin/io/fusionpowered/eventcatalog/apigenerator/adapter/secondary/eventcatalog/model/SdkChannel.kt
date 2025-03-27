package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

import js.objects.Record

@JsExport
class SdkChannel(
  val id: String,
  val name: String,
  val version: String,
  val summary: String?,
  val address: String?,
  val protocols: Array<String>?,
  val parameters: Record<String, SdkParameter>?,
  val markdown: String,
)