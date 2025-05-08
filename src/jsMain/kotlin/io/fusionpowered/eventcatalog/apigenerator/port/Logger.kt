package io.fusionpowered.eventcatalog.apigenerator.port

@JsExport
interface Logger {

  fun debug(message: String)

  fun info(message: String)

  fun warn(message: String)

  fun error(message: String)

  fun highlightedInfo(message: String)
}