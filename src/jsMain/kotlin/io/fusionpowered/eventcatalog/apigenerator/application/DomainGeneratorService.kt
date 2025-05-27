package io.fusionpowered.eventcatalog.apigenerator.application

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsLogger
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain
import io.fusionpowered.eventcatalog.apigenerator.model.import.DomainImportData
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog
import io.fusionpowered.eventcatalog.apigenerator.port.Logger

class DomainGeneratorService(
  private val catalog: EventCatalog = EventCatalogAdapter(),
  private val logger: Logger = NodejsLogger,
) {

  suspend fun generate(domainImportData: DomainImportData): Domain =
    Domain(domainImportData)
      .apply { logger.highlightedInfo("\nProcessing domain: $name (v$version)") }
      .let { domain ->
        val latestDomain = catalog.getDomain(domain.id)
        when {
          latestDomain == null ->
            domain
              .apply {
                catalog.writeDomain(this)
                logger.info(" - Domain (v$version) created")
              }

          latestDomain.version != domain.version ->
            domain
              .copy(
                summary = latestDomain.summary,
                services = (domain.services + latestDomain.services).toMutableList(),
                owners = domain.owners + latestDomain.owners,
                entities = domain.entities + latestDomain.entities,
                markdown = latestDomain.markdown
              )
              .apply {
                catalog.versionDomain(id)
                logger.warn(" - Versioned previous domain (v${latestDomain.version})")
                catalog.writeDomain(this)
                logger.info(" - Domain (v$version) created")
              }

          else ->
            domain
              .copy(
                summary = latestDomain.summary,
                services = (domain.services + latestDomain.services).toMutableList(),
                owners = domain.owners + latestDomain.owners,
                entities = domain.entities + latestDomain.entities,
                markdown = latestDomain.markdown
              )
              .apply { logger.warn(" - Domain (v$version) already exists, skipped creation...") }
        }
      }

}