package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.jsModule

@JsModule("chalk")
@JsNonModule
external class Chalk {

  companion object {

    fun red(message: String): String

    fun yellow(message: String): String

    fun green(message: String): String

    fun cyan(message: String): String

    fun blue(message: String): String

  }

}