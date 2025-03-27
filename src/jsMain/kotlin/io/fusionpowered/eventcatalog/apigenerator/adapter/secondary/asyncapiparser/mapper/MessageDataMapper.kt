package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiChannel
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiInfo
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.AsyncapiMessage
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Direction.Receives
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Direction.Sends
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role.Consumed
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role.Provided
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Type.Event
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData.Message.Payload
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData.Message.Payload.Format.Json
import io.fusionpowered.eventcatalog.common.circularStringify
import io.fusionpowered.eventcatalog.common.extensions

fun AsyncapiMessage.toMessage(
  documentInfo: AsyncapiInfo,
  channels: Array<AsyncapiChannel>,
  operationAction: String?,
  operationRole: String?
) =
  AsyncapiData.Message(
    id = id().lowercase(),
    name = title() ?: id(),
    version = json().extensions["x-eventcatalog-message-version"] ?: documentInfo.version(),
    summary = summary() ?: "",
    description = description() ?: "",
    type = json().extensions["x-eventcatalog-message-type"]?.toType() ?: Event,
    direction = when (operationAction) {
      "receive", "subscribe" -> Receives
      "send", "publish" -> Sends
      else -> throw IllegalStateException("Unknown action $operationAction")
    },
    externalDocumentation = externalDocs()?.toExternalDocumentation(),
    tags = tags()?.all()?.map { it.name() } ?: emptyList(),
    role = when (operationRole) {
      "provider" -> Provided
      else -> Consumed
    },
    payload = payload()?.json()
      ?.let { payloadJson ->
        Payload(
          schema = circularStringify(payloadJson.extensions["x-parser-original-payload"] ?: payloadJson),
          format = schemaFormat()?.toFormat() ?: Json
        )
      },
    channels = channels.map { it.toChannelData(documentInfo) }
  )