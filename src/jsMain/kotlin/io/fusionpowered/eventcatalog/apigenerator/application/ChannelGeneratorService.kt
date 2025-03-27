package io.fusionpowered.eventcatalog.apigenerator.application

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.EventCatalogAdapter
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.NodejsLogger
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Channel
import io.fusionpowered.eventcatalog.apigenerator.port.EventCatalog
import io.fusionpowered.eventcatalog.apigenerator.port.Logger

class ChannelGeneratorService(
  private val catalog: EventCatalog = EventCatalogAdapter(),
  private val logger: Logger = NodejsLogger,
) {

  suspend fun generate(channelApiData: AsyncapiData.Channel): Channel =
    Channel(channelApiData)
      .apply { logger.highlightedInfo("Processing channel: $name (v$version)") }
      .let { channel ->
        val latestChannel = catalog.getChannel(channel.id)
        when {
          latestChannel == null ->
            channel
              .apply {
                catalog.writeChannel(this)
                logger.info(" - Channel (v$version) created")
              }

          latestChannel.version != channel.version ->
            channel
              .copy(
                markdown = latestChannel.markdown
              )
              .apply {
                catalog.versionChannel(id)
                logger.warn(" - Versioned previous channel (v${latestChannel.version})")
                catalog.writeChannel(this)
                logger.info(" - Channel (v$version) created")
              }

          else ->
            channel
              .copy(
                markdown = latestChannel.markdown
              )
              .apply {
                logger.warn(" - Channel (v$version) already exists, overwriting previous channel...")
                catalog.writeChannel(this)
                logger.info(" - Channel (v$version) created")
              }
        }
      }


}