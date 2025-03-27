package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model

@JsExport
class ServiceProperty(
  val id: String,
  val name: String? = null,
  val openapiPath: String? = null,
  val asyncapiPath: String? = null,
  val owners: Array<String>? = null
) {

  init {
    require(openapiPath != null || asyncapiPath != null) {
      "At least one specification file path is required"
    }
  }

}