package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.jsModule.Parser
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.jsModule.createAvroSchemaParser
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.jsModule.fromFile
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.jsModule.fromURL
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper.toAsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.ParseOptions
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsLogger
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.port.DocumentReader
import io.fusionpowered.eventcatalog.apigenerator.port.Logger
import kotlinx.coroutines.await

class AsyncapiParserDocumentReader(
  private val logger: Logger = NodejsLogger
) : DocumentReader<AsyncapiData> {

  override suspend fun read(path: String): AsyncapiData {
    val document = try {
      Parser()
        .apply { registerSchemaParser(createAvroSchemaParser()); }
        .let { parser ->
          when {
            path.startsWith("http") -> fromURL(parser, path)
            else -> fromFile(parser, path)
          }
        }
        .parse(ParseOptions(parseSchemas = true)).await()
        .document
        ?: throw RuntimeException()
    } catch (throwable: Throwable) {
      logger.error("Failed to parse AsyncAPI file: $path")
      throw throwable
    }
    return document.toAsyncapiData(path)
  }

}