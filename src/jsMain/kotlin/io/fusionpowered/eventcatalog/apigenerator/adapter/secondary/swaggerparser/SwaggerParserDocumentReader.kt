package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsLogger
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.jsmodule.SwaggerParser
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper.toOpenapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData
import io.fusionpowered.eventcatalog.apigenerator.port.DocumentReader
import io.fusionpowered.eventcatalog.apigenerator.port.Logger
import kotlinx.coroutines.await

class SwaggerParserDocumentReader(
  private val logger: Logger = NodejsLogger
) : DocumentReader<OpenapiData> {

  override suspend fun read(path: String): OpenapiData {
    val document = try {
      SwaggerParser.validate(path).await()
      SwaggerParser.dereference(path).await()
    } catch (throwable: Throwable) {
      logger.error("Failed to parse OpenAPI file: $path")
      throw throwable
    }
    return document.toOpenapiData(path)
  }

}


