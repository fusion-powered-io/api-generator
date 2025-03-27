@file:JsModule("@asyncapi/parser")
@file:JsNonModule

package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.jsModule

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model.FromResult

external class Parser {

  fun registerSchemaParser(avroSchemaParser: dynamic)

}

external fun fromFile(parser: Parser, source: String, options: dynamic = definedExternally): FromResult

external fun fromURL(parser: Parser, source: String, options: dynamic = definedExternally): FromResult