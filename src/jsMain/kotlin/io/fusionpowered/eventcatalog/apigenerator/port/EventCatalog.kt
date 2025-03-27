package io.fusionpowered.eventcatalog.apigenerator.port

import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Direction
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Direction.Receives
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role.Provided
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Type
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Channel
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Message
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Service


interface EventCatalog {

  suspend fun getDomain(id: String, version: String = "latest"): Domain?

  suspend fun versionDomain(id: String)

  suspend fun writeDomain(domain: Domain)

  suspend fun getService(id: String, version: String = "latest"): Service?

  suspend fun versionService(id: String)

  suspend fun writeService(service: Service, domain: Domain? = null)

  suspend fun addFileToService(id: String, filename: String, content: String)

  suspend fun getMessage(id: String, version: String = "latest"): Message?

  suspend fun versionMessage(id: String, messageData: ApiData.Message)

  suspend fun writeMessage(message: Message, type: Type, role: Role = Provided, direction: Direction = Receives, service: Service, domain: Domain? = null)

  suspend fun addFileToMessage(id: String, messageData: ApiData.Message, filename: String, content: String)

  suspend fun getChannel(id: String, version: String = "latest"): Channel?

  suspend fun versionChannel(id: String)

  suspend fun writeChannel(channel: Channel)

}