package io.fusionpowered.eventcatalog.apigenerator.integration

import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration.catalog
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration.catalogDirSetup
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration.catalogDirTeardown
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration.getAsyncapiExample
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration.getOpenapiExample
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.DomainProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.ServiceProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.plugin
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfiguration.catalogDir
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ResourcePointer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.await
import node.fs.existsSync

class Domain : StringSpec({

  beforeEach(catalogDirSetup)

  afterEach(catalogDirTeardown)

  "if a domain is not configured, the defined service is not added to any domains" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val undefinedDomainId = "orders"

    //when
    plugin(
      properties = Properties(arrayOf(service)),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getDomain(undefinedDomainId) shouldBe null
  }

  "if a domain is configured and it does not exist, it is created" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0"
    )
    val versionInOpenapiFile = "1.0.0"

    //when
    plugin(
      properties = Properties(arrayOf(service), domain),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getDomain(domain.id) shouldNotBeNull {
      id shouldBe domain.id
      name shouldBe domain.name
      version shouldBe domain.version
      services shouldContainExactly listOf(ResourcePointer(service.id, versionInOpenapiFile))
    }
  }

  "if a domain is configured its version does not match the existing domain, the existing domain is versioned and a new one is created" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val versionInOpenapiFile = "1.0.0"
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0"
    )
    val versionOfExistingDomain = "0.0.1"
    catalog.writeDomain(
      Domain(
        id = domain.id,
        name = domain.name,
        version = versionOfExistingDomain
      )
    )

    //when
    plugin(
      properties = Properties(arrayOf(service), domain),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getDomain(domain.id, versionOfExistingDomain) shouldNotBe null
    catalog.getDomain(domain.id, domain.version) shouldNotBeNull {
      services shouldContainExactly listOf(ResourcePointer(service.id, versionInOpenapiFile))
    }
  }

  "if a domain is configured and it already exists, the service is added to the domain" {
    //given
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0"
    )
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    val versionInOpenapiFile = "1.0.0"
    catalog.writeDomain(
      Domain(
        id = domain.id,
        name = domain.name,
        version = domain.version
      )
    )

    //when
    plugin(
      properties = Properties(arrayOf(service), domain),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getDomain(domain.id) shouldNotBeNull {
      services shouldContainExactly listOf(ResourcePointer(service.id, versionInOpenapiFile))
    }
  }

  "if a domain is configured with owners, the owners are written to the domain" {
    //given
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0",
      owners = arrayOf("John Doe", "Jane Doe")
    )

    //when
    plugin(
      properties = Properties(emptyArray(), domain),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getDomain(domain.id) shouldNotBeNull {
      version shouldBe domain.version
      owners shouldContainExactly domain.owners!!.toSet()
    }
  }

  "if a domain is configured along with a service, the service is written to the domain" {
    //given
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0"
    )
    val openapiService = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val versionInOpenapiFile = "1.0.0"
    val asyncapiService = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    val versionInAsyncapiFile = "1.0.0"

    //when
    plugin(
      properties = Properties(arrayOf(openapiService, asyncapiService), domain),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    catalog.getDomain(domain.id) shouldNotBeNull {
      services shouldContainExactly listOf(
        ResourcePointer(openapiService.id, versionInOpenapiFile),
        ResourcePointer(asyncapiService.id, versionInAsyncapiFile)
      )
    }
  }

  "if a domain is configured as the latest along with a service, no versioned folder is created in domain" {
    //given
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0"
    )
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    catalog.writeDomain(
      Domain(
        id = domain.id,
        name = domain.name,
        version = domain.version
      )
    )

    //when
    plugin(
      properties = Properties(arrayOf(service), domain),
      generator = ApiGeneratorService(catalog)
    ).await()

    //then
    existsSync("${catalogDir}/domain/${domain.id}/versioned") shouldBe false
  }

})
