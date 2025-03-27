package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiDocument
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.pathMap
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.toMap
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData
import node.path.path


fun OpenapiDocument.toOpenapiData(documentPath: String) =
  OpenapiData(
    service = OpenapiData.Service(
      name = info.title,
      version = info.version,
      schemaPath = path.basename(documentPath),
      description = info.description ?: "",
      tags = tags?.map { it.name } ?: emptyList(),
      externalDocumentation = externalDocs?.toExternalDocumentation()
    ),
    messages = pathMap
      .flatMap { (path, openapiMethodToOperationMapReference) ->
        openapiMethodToOperationMapReference.toMap()
          .map { (method, operation) ->
            operation.toMessageData(path, info, method)
          }
      }
  )