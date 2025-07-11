package io.fusionpowered.eventcatalog.apigenerator.application

import io.fusionpowered.eventcatalog.apigenerator.ApiGenerator
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.AsyncapiParserDocumentReader
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.SwaggerParserDocumentReader
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData
import io.fusionpowered.eventcatalog.apigenerator.model.import.DomainImportData
import io.fusionpowered.eventcatalog.apigenerator.model.import.ServiceImportData
import io.fusionpowered.eventcatalog.apigenerator.port.DocumentReader
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog

class ApiGeneratorService(
  catalog: EventCatalog = EventCatalogAdapter(),
  private val openapiDocumentReader: DocumentReader<OpenapiData> = SwaggerParserDocumentReader(),
  private val asyncapiDocumentReader: DocumentReader<AsyncapiData> = AsyncapiParserDocumentReader(),
  private val domainGenerator: DomainGeneratorService = DomainGeneratorService(catalog),
  private val serviceGenerator: ServiceGeneratorService = ServiceGeneratorService(catalog),
  private val channelGenerator: ChannelGeneratorService = ChannelGeneratorService(catalog),
  private val messageGenerator: MessageGeneratorService = MessageGeneratorService(catalog),
) : ApiGenerator {

  override suspend fun generate(servicesImportData: Set<ServiceImportData>, domainImportData: DomainImportData?) {
    val domain = domainImportData?.let { domainGenerator.generate(it) }

    servicesImportData.forEach { serviceImportData ->
      when {
        serviceImportData.openapiPath != null && serviceImportData.asyncapiPath != null -> {
          //Not yet implemented
        }

        serviceImportData.openapiPath != null && serviceImportData.asyncapiPath == null ->
          openapiDocumentReader.read(serviceImportData.openapiPath)
            .let { openapiData -> openapiData to serviceGenerator.generate(domain, serviceImportData, openapiData.service) }
            .let { (openapiData, service) -> openapiData.messages.forEach { messageGenerator.generate(domain, service, it) } }

        serviceImportData.openapiPath == null && serviceImportData.asyncapiPath != null ->
          asyncapiDocumentReader.read(serviceImportData.asyncapiPath)
            .let { asyncapiData -> asyncapiData to serviceGenerator.generate(domain, serviceImportData, asyncapiData.service) }
            .let { (asyncapiData, service) ->
              asyncapiData.channels.forEach { channelGenerator.generate(it) }
              asyncapiData.messages.forEach { messageGenerator.generate(domain, service, it) }
            }
      }
    }
  }

}