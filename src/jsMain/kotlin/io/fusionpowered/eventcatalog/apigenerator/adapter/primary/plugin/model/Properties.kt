package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model


@JsExport
class Properties(
  val services: Array<ServiceProperty>,
  val domain: DomainProperty? = null
)