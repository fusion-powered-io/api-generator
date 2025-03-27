package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.asyncapiparser.model

@JsExport
external interface AsyncapiCollection<T> {

  fun all(): Array<T>

  fun has(id: String): Boolean

  fun filterBy(filter: (item: T) -> Boolean): Array<T>

}
