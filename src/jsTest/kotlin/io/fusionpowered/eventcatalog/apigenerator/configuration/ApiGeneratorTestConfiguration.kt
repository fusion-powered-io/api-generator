package io.fusionpowered.eventcatalog.apigenerator.configuration

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog
import io.kotest.core.spec.AfterTest
import io.kotest.core.spec.BeforeTest
import js.objects.unsafeJso
import node.fs.existsSync
import node.fs.mkdirSync
import node.fs.rmSync
import node.path.path

object ApiGeneratorTestConfiguration {

  val catalog: EventCatalog = EventCatalogAdapter(path.resolve("catalog"))

  val catalogDirSetup: BeforeTest = {
    if (existsSync(catalog.directory)) {
      rmSync(catalog.directory, unsafeJso { recursive = true })
    }
    mkdirSync(catalog.directory)
  }

  val catalogDirTeardown: AfterTest = {
    if (existsSync(catalog.directory)) {
      rmSync(catalog.directory, unsafeJso { recursive = true })
    }
  }

  fun getOpenapiExample(name: String) =
    path.join("kotlin/openapi", name)

  fun getAsyncapiExample(name: String) =
    path.join("kotlin/asyncapi", name)

}