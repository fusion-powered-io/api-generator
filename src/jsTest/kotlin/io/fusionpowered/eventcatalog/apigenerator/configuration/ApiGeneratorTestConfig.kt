package io.fusionpowered.eventcatalog.apigenerator.configuration

import io.fusionpowered.eventcatalog.apigenerator.extensions.CatalogExtension
import io.kotest.core.config.AbstractProjectConfig
import node.path.path

class ApiGeneratorTestConfig: AbstractProjectConfig() {

  override val extensions = listOf(CatalogExtension)

  companion object {
    fun getOpenapiExample(name: String) =
      path.join("kotlin/openapi", name)

    fun getAsyncapiExample(name: String) =
      path.join("kotlin/asyncapi", name)
  }

}