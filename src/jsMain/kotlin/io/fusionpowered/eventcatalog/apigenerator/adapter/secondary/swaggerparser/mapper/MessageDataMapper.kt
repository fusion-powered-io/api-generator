package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.jsmodule.slugify
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiInfo
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.OpenapiOperation
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.model.responses
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData.Message
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Direction
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Type
import io.fusionpowered.eventcatalog.common.extensions

fun OpenapiOperation.toMessageData(path: String, info: OpenapiInfo, method: String): Message {
  val alternativeId = operationId ?: "${slugify(info.title)}_${method.uppercase()}_${path.replace("/", "")}"
    .trim('_')
  return Message(
    id = extensions["x-eventcatalog-message-id"] ?: alternativeId,
    name = extensions["x-eventcatalog-message-name"] ?: alternativeId,
    version = extensions["x-eventcatalog-message-version"] ?: info.version,
    path = path,
    method = method.uppercase(),
    summary = summary ?: "",
    description = description ?: "",
    type = extensions["x-eventcatalog-message-type"]?.toType() ?: Type.Query,
    direction = when {
      extensions["x-eventcatalog-message-action"] === "sends" -> Direction.Sends
      else -> Direction.Receives
    },
    externalDocumentation = externalDocs?.toExternalDocumentation(),
    tags = tags?.toList() ?: emptyList(),
    request = Message.Request(
      parameters = parameters?.map { it.toParameter() } ?: emptyList(),
      body = request?.toBody(),
    ),
    responses = responses.mapValues { it.toResponse() }
  )
}
