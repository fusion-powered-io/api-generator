package io.fusionpowered.eventcatalog.apigenerator.application

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsGitRepositoryConfig
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsLogger
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Message
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Service
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog
import io.fusionpowered.eventcatalog.apigenerator.port.Logger
import io.fusionpowered.eventcatalog.apigenerator.port.RepositoryConfig
import kotlin.js.Date

class MessageGeneratorService(
  private val catalog: EventCatalog = EventCatalogAdapter(),
  private val logger: Logger = NodejsLogger,
  private val repositoryConfig: RepositoryConfig = NodejsGitRepositoryConfig,
) {

  suspend fun generate(domain: Domain?, service: Service, messageApiData: ApiData.Message): Message =
    Message(messageApiData)
      .apply { logger.highlightedInfo("Processing message: $name (v$version)") }
      .let { message -> message.copy(owners = message.owners + service.owners) }
      .let { message ->
        val remoteUrl = repositoryConfig.remoteUrl
        when (remoteUrl.isNotBlank()) {
          true -> {
            val relativeCatalogDir = node.path.path.relative(repositoryConfig.topLevelDirectory, catalog.directory)
            val messageDir = when (messageApiData.type) {
              ApiData.Message.Type.Event -> "events"
              ApiData.Message.Type.Query -> "queries"
              ApiData.Message.Type.Command -> "commands"
            }
            val indexFile = when {
              domain == null -> "services/${service.id}/$messageDir/${message.id}/index.mdx"
              else -> "domains/${domain.id}/services/${service.id}/$messageDir/index.mdx"
            }
            message.copy(editUrl = "$remoteUrl/blob/${repositoryConfig.defaultBranch}/$relativeCatalogDir/$indexFile")
          }

          false -> message
        }
      }
      .let { message ->
        val latestMessage = catalog.getMessage(message.id)
        when {
          latestMessage == null -> message
            .apply {
              catalog.writeMessage(
                message = this,
                type = messageApiData.type,
                role = messageApiData.role,
                direction = messageApiData.direction,
                service = service,
                domain = domain
              )
              addSchemas(messageApiData)
              addChangelog(messageApiData)
              logger.info(" - Message (v$version) created")
            }

          latestMessage.version != message.version ->
            message
              .copy(
                badges = message.badges + latestMessage.badges,
                owners = message.owners + latestMessage.owners,
                markdown = latestMessage.markdown
              )
              .apply {
                catalog.versionMessage(id, messageApiData)
                logger.info(" - Versioned previous message: (v$version)")
                catalog.writeMessage(
                  message = this,
                  type = messageApiData.type,
                  role = messageApiData.role,
                  direction = messageApiData.direction,
                  service = service,
                  domain = domain
                )
                addSchemas(messageApiData)
                addChangelog(messageApiData)
                logger.info(" - Message (v$version) created")
              }

          else ->
            message
              .copy(
                badges = message.badges + latestMessage.badges,
                owners = message.owners + latestMessage.owners,
                markdown = latestMessage.markdown
              )
              .apply {
                logger.warn(" - Message (v$version) already exists, overwriting previous message...")
                catalog.writeMessage(
                  message = this,
                  type = messageApiData.type,
                  role = messageApiData.role,
                  direction = messageApiData.direction,
                  service = service,
                  domain = domain
                )
                addSchemas(messageApiData)
                logger.info(" - Message (v$version) created")
              }
        }
      }

  private suspend fun Message.addSchemas(messageData: ApiData.Message) {

    when (messageData) {
      is OpenapiData.Message -> {
        messageData.request.body?.content
          ?.run {
            catalog.addFileToMessage(
              id = id,
              messageData = messageData,
              filename = "request-body.json",
              content = value
            )
          }

        messageData.responses.forEach { (statusCode, response) ->
          response.body.content
            ?.run {
              catalog.addFileToMessage(
                id = id,
                messageData = messageData,
                filename = "response-$statusCode.json",
                content = value
              )
            }
        }
      }

      is AsyncapiData.Message -> {
        messageData.payload
          ?.run {
            catalog.addFileToMessage(
              id = id,
              messageData = messageData,
              filename = "schema.${format.extension}",
              content = schema
            )
          }
      }
    }

  }

  private suspend fun Message.addChangelog(messageData: ApiData.Message) {
    catalog.addFileToMessage(
      id = id,
      messageData = messageData,
      filename = "changelog.mdx",
      content = "---\ncreatedAt: ${Date().toISOString().split('T')[0]}\n---\n"
    )
  }

}