package io.fusionpowered.eventcatalog.apigenerator.integration

import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.DomainProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.ServiceProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.plugin
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getAsyncapiExample
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getOpenapiExample
import io.fusionpowered.eventcatalog.apigenerator.extensions.CatalogExtension.catalog
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Domain
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ResourcePointer
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import node.fs.existsSync


class Domain : StringSpec({

  "if a domain is not configured, the defined service is not added to any domains" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val undefinedDomainId = "orders"

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

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
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

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
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getDomain(domain.id, versionOfExistingDomain) shouldNotBe null
    catalog.getDomain(domain.id, domain.version) shouldNotBeNull {
      services shouldContainExactly listOf(ResourcePointer(service.id, versionInOpenapiFile))
    }
  }

  "if a domain is configured and already exists, services, owners, entities and markdown are persisted" {
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
    val alreadyExistingDomain = Domain(
      id = domain.id,
      name = domain.name,
      summary = "This is the existing summary",
      version = "0.0.1",
      services = mutableListOf(ResourcePointer("otherService", "1.0.0")),
      owners = setOf("jbrandao"),
      entities = setOf("Entity1"),
      markdown = "This is the existing markdown"
    )
    catalog.writeDomain(alreadyExistingDomain)

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getDomain(domain.id) shouldNotBeNull {
      id shouldBe domain.id
      name shouldBe domain.name
      version shouldBe domain.version
      summary shouldBe alreadyExistingDomain.summary
      services shouldContainExactly alreadyExistingDomain.services + ResourcePointer(service.id, versionInOpenapiFile)
      markdown shouldBe alreadyExistingDomain.markdown
      owners shouldContainAll alreadyExistingDomain.owners
      entities shouldContainAll alreadyExistingDomain.entities
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
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

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
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(emptyArray(), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

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
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(openapiService, asyncapiService), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

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
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    existsSync("${catalog.directory}/domains/${domain.id}/versioned") shouldBe false
  }

  "if catalog is in a initialized repository, then assign the domain url to the editUrl" {
    //given
    val domain = DomainProperty(
      id = "orders",
      name = "Orders Domain",
      version = "1.0.0"
    )
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml"),
      owners = arrayOf("John Doe", "Jane Doe")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getDomain(domain.id) shouldNotBeNull {
      editUrl shouldBe "https://github.com/fusion-powered-io/api-generator/blob/main/build/js/packages/@fusionpowered/api-generator-test/catalog/domains/$id/index.mdx"
    }
  }

})

