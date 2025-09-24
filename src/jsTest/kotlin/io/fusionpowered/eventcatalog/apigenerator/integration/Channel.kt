package io.fusionpowered.eventcatalog.apigenerator.integration

import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.ServiceProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.plugin
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getAsyncapiExample
import io.fusionpowered.eventcatalog.apigenerator.extensions.CatalogExtension.catalog
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Channel
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Parameter
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ResourcePointer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContain
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import kotlinx.coroutines.await

class Channel : StringSpec({

  "if a channel is defined in  asyncapi specification, it is created" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )
    val versionInAsyncapiFile = "1.0.0"
    var channelsInAsyncapiFile = listOf("lightingMeasured", "lightTurnOn", "lightTurnOff", "lightsDim")

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    channelsInAsyncapiFile.forEach { channel ->
      catalog.getChannel(channel) shouldNotBe null
    }
    catalog.getChannel("lightingMeasured") shouldNotBeNull {
      id shouldBe "lightingMeasured"
      name shouldBe "Lighting Measured Channel"
      version shouldBe versionInAsyncapiFile
      address shouldBe "smartylighting.streetlights.1.0.event.{streetlightId}.lighting.measured"
      summary shouldBe "Inform about environmental lighting conditions of a particular streetlight."
      protocols shouldContainExactly listOf("kafka")
      parameters shouldBe mapOf(
        "streetlightId" to Parameter(
          enum = emptyList(),
          default = "",
          examples = emptyList(),
          description = "The ID of the streetlight."
        )
      )
      markdown shouldContain """
        ## Overview
        The topic on which measured values may be produced and consumed.
        <ChannelInformation />
      """.trimIndent()
    }
  }

  "if a channel has no address, parameters, title or description, it is still written to the catalog" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )
    val versionInAsyncapiFileInVersionExtension = "2.0.0"

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getChannel("lightsDim") shouldNotBeNull {
      id shouldBe "lightsDim"
      name shouldBe "lightsDim"
      version shouldBe versionInAsyncapiFileInVersionExtension
      address shouldBe ""
      summary shouldBe ""
      protocols shouldBe emptySet()
      parameters shouldBe emptyMap()
      markdown shouldBe "<ChannelInformation />"
    }
  }

  "if a channel has messages, it appears referenced by the messages" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getMessage("lightmeasured") shouldNotBeNull {
      channels shouldContain ResourcePointer("lightingMeasured", "1.0.0")
    }
  }

  "if a channel has `x-eventcatalog-channel-version`, its version is used instead of the asyncapi specification version" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )
    val versionInAsyncapiFileInVersionExtension = "2.0.0"

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getChannel("lightsDim") shouldNotBeNull {
      version shouldBe versionInAsyncapiFileInVersionExtension
    }
  }

  "if a channel already exists and the versions match, the metadata is updated and the markdown is persisted" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )
    val channel = Channel(
      id = "lightingMeasured",
      name = "Lighting Measured Channel",
      version = "1.0.0",
      markdown = "please dont override me!"
    )
    catalog.writeChannel(channel)

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getChannel("lightingMeasured") shouldNotBeNull {
      markdown shouldBe "please dont override me!"
    }
  }

  "if a channel already exists and the versions do not match, the existing channel is versioned" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )
    val channel = Channel(
      id = "lightingMeasured",
      name = "Lighting Measured Channel",
      version = "0.0.5"
    )
    catalog.writeChannel(channel)

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getChannel("lightingMeasured", "0.0.5") shouldNotBe null
    catalog.getChannel("lightingMeasured", "1.0.0") shouldNotBe null
  }

  "if catalog is in a initialized repository, then assign the channel url to the editUrl" {
    //given
    val service = ServiceProperty(
      id = "streetlights-service",
      asyncapiPath = getAsyncapiExample("streetlights-kafka-asyncapi.yml")
    )

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getChannel("lightsDim") shouldNotBeNull {
      editUrl shouldBe "https://github.com/fusion-powered-io/api-generator/blob/main/build/js/packages/@fusionpowered/api-generator-test/catalog/channels/$id/index.mdx"
    }
  }

})
