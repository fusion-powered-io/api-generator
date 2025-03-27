package io.fusionpowered.eventcatalog.apigenerator.port

import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData

interface DocumentReader<T : ApiData> {

  suspend fun read(path: String): T

}