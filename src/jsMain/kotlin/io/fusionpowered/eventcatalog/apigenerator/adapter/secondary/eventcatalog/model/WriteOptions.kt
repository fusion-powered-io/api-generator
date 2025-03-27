package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

@JsExport
data class WriteOptions(
  val path: String = "",
  val override: Boolean = false,
  val versionExistingContent: Boolean = false
)