package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.jsmodule

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.Sdk


@JsModule("@eventcatalog/sdk")
@JsNonModule
external fun eventCatalogSdk(path: String): Sdk