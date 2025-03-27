package io.fusionpowered.eventcatalog.common

fun circularStringify(jsObject: Any?): String {
  try {
    return JSON.stringify(jsObject, null, 2)
  } catch (_: Throwable) {
    val seen = mutableSetOf<String>()
    return JSON.stringify(
      jsObject,
      { _, value ->
        if (jsTypeOf(value) === "object" && value !== undefined) {
          if (seen.contains(value)) return@stringify "[Circular]"
          seen.add(value.unsafeCast<String>())
        }
        return@stringify value
      },
      2
    )
  }
}