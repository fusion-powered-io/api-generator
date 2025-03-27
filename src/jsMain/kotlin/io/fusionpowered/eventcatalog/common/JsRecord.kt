package io.fusionpowered.eventcatalog.common

import js.core.JsAny
import js.objects.Record

fun <K : JsAny, V : JsAny> Map<K, V>.toRecord(): Record<K, V> {
  val record = Record<K, V>()
  forEach { (key, value) ->
    record[key] = value
  }
  return record
}