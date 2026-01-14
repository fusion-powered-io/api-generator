package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

@JsExport
class SdkReceivesPointer(
  val id: String,
  val version: String,
  val from: Array<SdkResourcePointer>?
)