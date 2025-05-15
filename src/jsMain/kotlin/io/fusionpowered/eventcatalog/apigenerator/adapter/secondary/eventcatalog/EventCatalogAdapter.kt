package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.jsmodule.eventCatalogSdk
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper.*
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkFile
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkResourcePointer
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.WriteOptions
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Role.Provided
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Type.*
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.*
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog
import kotlinx.coroutines.await

private external val process: dynamic

class EventCatalogAdapter(
  catalogDir: String = process.env["PROJECT_DIR"].unsafeCast<String?>() ?: process.cwd().unsafeCast<String>(),
) : EventCatalog {

  private val sdk = eventCatalogSdk(catalogDir)

  override suspend fun getDomain(id: String, version: String): Domain? {
    return sdk.getDomain(id, version).await()?.toDomain()
  }

  override suspend fun versionDomain(id: String) {
    sdk.versionDomain(id).await()
  }

  override suspend fun writeDomain(domain: Domain) {
    sdk.writeDomain(domain.toSdkDomain(), WriteOptions(override = true)).await()
  }

  override suspend fun getService(id: String, version: String): Service? {
    return sdk.getService(id, version).await()?.toService()
  }

  override suspend fun versionService(id: String) {
    sdk.versionService(id).await()
  }

  override suspend fun writeService(service: Service, domain: Domain?) {
    when {
      domain == null -> sdk.writeService(service.toSdkService(), WriteOptions(override = true)).await()
      else -> domain
        .also { sdk.writeServiceToDomain(service.toSdkService(), SdkResourcePointer(it.id, "latest"), WriteOptions(override = true)).await() }
        .apply {
          when (val indexOfService = services.indexOfFirst { it.id == service.id }) {
            -1 -> services.add(ResourcePointer(service.id, service.version))
            else -> services[indexOfService] = ResourcePointer(service.id, service.version)
          }
        }
        .let { sdk.writeDomain(it.toSdkDomain(), WriteOptions(override = true)).await() }
    }
  }

  override suspend fun addFileToService(id: String, filename: String, content: String) {
    sdk.addFileToService(id, SdkFile(filename, content), "latest").await()
  }

  override suspend fun getMessage(id: String, version: String): Message? {
    return sdk.getEvent(id, version).await()?.toMessage()
  }

  override suspend fun versionMessage(id: String, messageData: ApiData.Message) {
    if (messageData.role != Provided) {
      return
    }
    when (messageData.type) {
      Event -> sdk.versionEvent(id).await()
      Command -> sdk.versionCommand(id).await()
      Query -> sdk.versionQuery(id).await()
    }
  }

  override suspend fun writeMessage(
    message: Message,
    type: ApiData.Message.Type,
    role: ApiData.Message.Role,
    direction: ApiData.Message.Direction,
    service: Service,
    domain: Domain?
  ) {
    when (direction) {
      ApiData.Message.Direction.Sends -> service.sends.add(ResourcePointer(message.id, message.version))
      ApiData.Message.Direction.Receives -> service.receives.add(ResourcePointer(message.id, message.version))
    }
    writeService(service, domain)
    if (role == Provided) {
      when (type) {
        Event -> sdk.writeEventToService(message.toSdkMessage(), SdkResourcePointer(service.id, "latest"), WriteOptions(override = true)).await()
        Command -> sdk.writeCommandToService(message.toSdkMessage(), SdkResourcePointer(service.id, "latest"), WriteOptions(override = true)).await()
        Query -> sdk.writeQueryToService(message.toSdkQuery(), SdkResourcePointer(service.id, "latest"), WriteOptions(override = true)).await()
      }
    }
  }

  override suspend fun addFileToMessage(id: String, messageData: ApiData.Message, filename: String, content: String) {
    if (messageData.role != Provided) {
      return
    }
    when (messageData.type) {
      Event -> sdk.addFileToEvent(id, SdkFile(filename, content), "latest").await()
      Command -> sdk.addFileToCommand(id, SdkFile(filename, content), "latest").await()
      Query -> sdk.addFileToQuery(id, SdkFile(filename, content), "latest").await()
    }
  }

  override suspend fun getChannel(id: String, version: String): Channel? {
    return sdk.getChannel(id, version).await()?.toChannel()
  }

  override suspend fun versionChannel(id: String) {
    sdk.versionChannel(id).await()
  }

  override suspend fun writeChannel(channel: Channel) {
    sdk.writeChannel(channel.toSdkChannel(), WriteOptions(override = true)).await()
  }

}