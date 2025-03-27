package io.fusionpowered.eventcatalog.common

external interface ExtendedApi

val ExtendedApi.extensions: Map<String, String?>
  get() =
    mapOf<String, String>(this)
      .filter { (propertyName, _) -> propertyName.startsWith("x-") }
