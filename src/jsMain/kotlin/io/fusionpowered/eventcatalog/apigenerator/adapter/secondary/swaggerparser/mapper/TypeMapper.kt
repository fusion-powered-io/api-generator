package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.mapper

import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData


fun String.toType() =
  ApiData.Message.Type.valueOf(replaceFirstChar(Char::uppercase))