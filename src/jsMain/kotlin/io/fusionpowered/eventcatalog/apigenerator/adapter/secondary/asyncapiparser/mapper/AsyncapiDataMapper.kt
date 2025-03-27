package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiDocument
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.common.extensions
import node.path.path

fun AsyncapiDocument.toAsyncapiData(documentPath: String) =
  AsyncapiData(
    service = AsyncapiData.Service(
      name = info().title(),
      version = info().version(),
      schemaPath = path.basename(documentPath),
      description = info().description() ?: "",
      tags = info().tags()?.all()?.map { it.name() } ?: emptyList(),
      externalDocumentation = info().externalDocs()?.toExternalDocumentation()
    ),
    channels = allChannels().all().map { it.toChannelData(info()) },
    messages = allOperations().all()
      .flatMap { operation -> operation.messages().all().map { operation to it } }
      .map { (operation, asyncapiMessage) ->
        asyncapiMessage.toMessage(
          documentInfo = info(),
          channels = allChannels().filterBy { it.messages().has(asyncapiMessage.id()) },
          operationAction = operation.action(),
          operationRole = operation.json().extensions["x-eventcatalog-role"]
        )
      }
  )