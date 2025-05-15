package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model

import kotlin.js.Promise


external interface Sdk {

  fun getDomain(id: String, version: String?): Promise<SdkDomain?>

  fun versionDomain(id: String): Promise<Unit>

  fun writeDomain(domain: SdkDomain, options: WriteOptions): Promise<Unit>

  fun getService(id: String, version: String?): Promise<SdkService?>

  fun versionService(id: String): Promise<Unit>

  fun writeService(service: SdkService, options: WriteOptions): Promise<Unit>

  fun writeServiceToDomain(service: SdkService, domain: SdkResourcePointer, options: WriteOptions): Promise<Unit>

  fun addFileToService(id: String, file: SdkFile, version: String?): Promise<Unit>

  /**
   * Works for commands and queries too as it's all the same behind the scenes
   */
  fun getEvent(id: String, version: String?): Promise<SdkMessage?>

  fun versionEvent(id: String): Promise<Unit>

  fun writeEventToService(event: SdkMessage, service: SdkResourcePointer, options: WriteOptions): Promise<Unit>

  fun addFileToEvent(id: String, file: SdkFile, version: String?): Promise<Unit>

  fun versionCommand(id: String): Promise<Unit>

  fun writeCommandToService(command: SdkMessage, service: SdkResourcePointer, options: WriteOptions): Promise<Unit>

  fun addFileToCommand(id: String, file: SdkFile, version: String?): Promise<Unit>

  fun versionQuery(id: String): Promise<Unit>

  fun writeQueryToService(query: SdkQuery, service: SdkResourcePointer, options: WriteOptions): Promise<Unit>

  fun addFileToQuery(id: String, file: SdkFile, version: String?): Promise<Unit>

  fun getChannel(id: String, version: String?): Promise<SdkChannel?>

  fun versionChannel(id: String): Promise<Unit>

  fun writeChannel(channel: SdkChannel, options: WriteOptions): Promise<Unit>

}