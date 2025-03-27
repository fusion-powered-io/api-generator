package io.fusionpowered.eventcatalog.common


fun <K, T> mapOf(jsObject: dynamic): Map<K, T> = when {
  jsObject !== undefined -> entriesOf<K, T>(jsObject).toMap()
  else -> emptyMap()
}

private fun <K, T> entriesOf(jsObject: dynamic): List<Pair<K, T>> =
  (js("Object.entries") as (dynamic) -> Array<Array<T>>)
    .invoke(jsObject)
    .map { entry -> entry[0].unsafeCast<K>() to entry[1] }
