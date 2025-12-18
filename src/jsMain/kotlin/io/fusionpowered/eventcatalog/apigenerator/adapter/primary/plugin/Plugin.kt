package io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin

import io.fusionpowered.eventcatalog.apigenerator.ApiGenerator
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.mapper.toDomainImportData
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.mapper.toServiceImportData
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService

@JsExport.Default
suspend fun plugin(
  @Suppress("UNUSED_PARAMETER") eventCatalogConfig: dynamic = null,
  pluginConfig: Properties,
  generator: ApiGenerator = ApiGeneratorService()
) {
    generator.generate(
      pluginConfig.services.map { it.toServiceImportData() }.toSet(),
      pluginConfig.domain?.toDomainImportData()
    )
}