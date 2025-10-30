package io.fusionpowered.eventcatalog.apigenerator.application

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsFileDownloader
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsFileSystem
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsGitRepositoryConfig
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsLogger
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Service
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Specifications
import io.fusionpowered.eventcatalog.apigenerator.model.import.ServiceImportData
import io.fusionpowered.eventcatalog.apigenerator.port.*
import kotlin.js.Date

class ServiceGeneratorService(
  private val catalog: EventCatalog = EventCatalogAdapter(),
  private val logger: Logger = NodejsLogger,
  private val repositoryConfig: RepositoryConfig = NodejsGitRepositoryConfig,
  private val fileSystem: FileSystem = NodejsFileSystem,
  private val fileDownloader: FileDownloader = NodejsFileDownloader
) {

  suspend fun generate(
    domain: Domain?,
    serviceImportData: ServiceImportData,
    openapiServiceData: OpenapiData.Service? = null,
    asyncapiServiceData: AsyncapiData.Service? = null
  ): Service {
    if (openapiServiceData != null && asyncapiServiceData != null) {
      throw IllegalStateException("We cannot generate a service with no serviceData.")
    }
    return Service(serviceImportData, openapiServiceData, asyncapiServiceData)
      .apply { logger.highlightedInfo("Processing service: $name (v$version)") }
      .let { service ->
        val remoteUrl = repositoryConfig.remoteUrl
        when (remoteUrl.isNotBlank()) {
          true -> {
            val relativeCatalogDir = node.path.path.relative(repositoryConfig.topLevelDirectory, catalog.directory)
            val indexFile = when (domain) {
              null -> "services/${service.id}/index.mdx"
              else -> "domains/${domain.id}/services/${service.id}/index.mdx"
            }
            service.copy(
              repository = service.repository.copy(url = remoteUrl),
              editUrl = "$remoteUrl/blob/${repositoryConfig.defaultBranch}/$relativeCatalogDir/$indexFile"
            )
          }

          false -> service
        }
      }
      .let { service ->
        val latestService = catalog.getService(service.id)
        when {
          latestService == null ->
            service
              .apply {
                catalog.writeService(this, domain)
                addSpecificationFiles(serviceImportData)
                addChangelog()
                logger.info(" - Service (v$version) created")
              }

          latestService.version != service.version ->
            service
              .copy(
                badges = service.badges + latestService.badges,
                sends = (service.sends + latestService.sends).toMutableList(),
                specifications = Specifications(
                  openapiPath = service.specifications.openapiPath.ifBlank { latestService.specifications.openapiPath },
                  asyncapiPath = service.specifications.asyncapiPath.ifBlank { latestService.specifications.asyncapiPath }
                ),
                owners = service.owners + latestService.owners,
                repository = latestService.repository,
                markdown = latestService.markdown,
              )
              .apply {
                catalog.versionService(id)
                logger.warn(" - Versioned previous service (v${latestService.version})")
                catalog.writeService(this, domain)
                addSpecificationFiles(serviceImportData)
                addChangelog()
                logger.info(" - Service (v$version) created")
              }

          else ->
            service
              .copy(
                badges = service.badges + latestService.badges,
                sends = (service.sends + latestService.sends).toMutableList(),
                specifications = Specifications(
                  openapiPath = service.specifications.openapiPath.ifBlank { latestService.specifications.openapiPath },
                  asyncapiPath = service.specifications.asyncapiPath.ifBlank { latestService.specifications.asyncapiPath }
                ),
                owners = service.owners + latestService.owners,
                repository = latestService.repository,
                markdown = latestService.markdown,
              )
              .apply {
                logger.warn(" - Service (v${service.version}) already exists,  overwriting previous service...")
                catalog.writeService(this, domain)
                addSpecificationFiles(serviceImportData)
                logger.info(" - Service (v$version) created")
              }
        }
      }
  }

  private suspend fun Service.addSpecificationFiles(serviceImportData: ServiceImportData) {
    serviceImportData.openapiPath
      ?.fetchContent()
      ?.let {
        catalog.addFileToService(
          id = id,
          filename = schemaPath,
          content = it
        )
      }

    serviceImportData.asyncapiPath
      ?.fetchContent()
      ?.let {
        catalog.addFileToService(
          id = id,
          filename = schemaPath,
          content = it
        )
      }
  }

  private suspend fun String.fetchContent() =
    when {
      startsWith("http") -> fileDownloader.download(this)
      else -> fileSystem.readFile(this)
    }

  private suspend fun Service.addChangelog() {
    catalog.addFileToService(
      id = id,
      filename = "changelog.mdx",
      content = "---\ncreatedAt: ${Date().toISOString().split('T')[0]}\n---\n"
    )
  }

}