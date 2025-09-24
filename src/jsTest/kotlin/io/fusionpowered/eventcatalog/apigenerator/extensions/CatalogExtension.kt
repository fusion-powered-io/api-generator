package io.fusionpowered.eventcatalog.apigenerator.extensions

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog
import io.kotest.core.listeners.AfterTestListener
import io.kotest.core.listeners.BeforeTestListener
import io.kotest.core.test.TestCase
import io.kotest.engine.test.TestResult
import js.objects.unsafeJso
import node.fs.existsSync
import node.fs.mkdirSync
import node.fs.rmSync
import node.path.path

object CatalogExtension: BeforeTestListener, AfterTestListener {

  val catalog: EventCatalog = EventCatalogAdapter(path.resolve("catalog"))

  override suspend fun beforeAny(testCase: TestCase) {
    if (existsSync(catalog.directory)) {
      rmSync(catalog.directory, unsafeJso { recursive = true })
    }
    mkdirSync(catalog.directory)
  }

  override suspend fun afterAny(testCase: TestCase, result: TestResult) {
    if (existsSync(catalog.directory)) {
      rmSync(catalog.directory, unsafeJso { recursive = true })
    }
  }

}