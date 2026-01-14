package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

@JsExport
class SdkSendsPointer(
  val id: String,
  val version: String,
  val to: Array<SdkResourcePointer>?
)