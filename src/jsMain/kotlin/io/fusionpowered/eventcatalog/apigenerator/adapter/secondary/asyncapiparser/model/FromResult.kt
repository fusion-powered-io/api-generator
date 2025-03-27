package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

import kotlin.js.Promise

@JsExport
external interface FromResult {

    fun parse(options: ParseOptions?): Promise<ParseOutput>

}