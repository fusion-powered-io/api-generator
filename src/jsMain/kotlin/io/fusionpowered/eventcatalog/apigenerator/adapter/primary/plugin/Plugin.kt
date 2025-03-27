package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin

import io.fusionpowered.eventcatalog.apigenerator.ApiGenerator
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.mapper.toDomainImportData
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.mapper.toServiceImportData
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.promise
import kotlin.js.Promise

@Suppress("NON_EXPORTABLE_TYPE")
@JsExport
fun plugin(
  properties: Properties,
  generator: ApiGenerator = ApiGeneratorService()
): Promise<Unit> {
  return GlobalScope.promise {
    generator.generate(
      properties.services.map { it.toServiceImportData() }.toSet(),
      properties.domain?.toDomainImportData()
    )
  }
}