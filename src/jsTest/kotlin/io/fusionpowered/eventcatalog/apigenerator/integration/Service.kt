package io.fusionpowered.eventcatalog.apigenerator.integration

import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.DomainProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.ServiceProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.plugin
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getAsyncapiExample
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getOpenapiExample
import io.fusionpowered.eventcatalog.apigenerator.extensions.CatalogExtension.catalog
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.*
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Service
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainAll
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import node.buffer.BufferEncoding.Companion.utf8
import node.fs.existsSync
import node.fs.readFileSync
import node.fs.readdirSync


class Service : StringSpec({

  "if a service is configured and it does not exist, it is created" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      id shouldBe service.id
      name shouldBe "Swagger Petstore"
      version shouldBe "1.0.0"
      summary shouldBe "This is a sample server Petstore server."
      badges shouldContainExactly setOf(Badge("Pets", "blue", "blue"))
    }
  }

  "if a service is configured with an openapi specification, it is written to the service " {
    //given
    val openapiSpecificationFile = "petstore.yml"
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample(openapiSpecificationFile),
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      specifications.openapiPath shouldBe openapiSpecificationFile
    }
    existsSync("${catalog.directory}/services/${service.id}/$openapiSpecificationFile") shouldBe true
  }

  "if a service is configured with an openapi specification, its endpoints are written as `receives` messages to the service" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      receives shouldContainExactly setOf(
        ReceivesPointer("list-pets", "5.0.0"),
        ReceivesPointer("createPets", "1.0.0"),
        ReceivesPointer("showPetById", "1.0.0"),
        ReceivesPointer("updatePet", "1.0.0"),
        ReceivesPointer("deletePet", "1.0.0"),
        ReceivesPointer("petAdopted", "1.0.0"),
      )
    }
  }

  "if a service is configured with an openapi specification and it already exists with `receives` messages, they are overwritten." {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    catalog.writeService(
      Service(
        id = service.id,
        version = "1.0.0",
        receives = mutableListOf(
          ReceivesPointer("messageToBeOverwritten", "1.0.0")
        )
      )
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      receives shouldContainExactly setOf(
        ReceivesPointer("list-pets", "5.0.0"),
        ReceivesPointer("createPets", "1.0.0"),
        ReceivesPointer("showPetById", "1.0.0"),
        ReceivesPointer("updatePet", "1.0.0"),
        ReceivesPointer("deletePet", "1.0.0"),
        ReceivesPointer("petAdopted", "1.0.0"),
      )
    }
  }

  "if a service is configured with an asyncapi specification, it is written to the service " {
    //given
    val asyncapiSpecificationFile = "simple.asyncapi.yml"
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample(asyncapiSpecificationFile),
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      specifications.asyncapiPath shouldBe asyncapiSpecificationFile
    }
    existsSync("${catalog.directory}/services/${service.id}/$asyncapiSpecificationFile") shouldBe true
  }

  "if a service is configured with an asyncapi specification and it already exists with an openapi specification, the asyncapi specification is added to it" {
    //given
    val asyncapiSpecificationFile = "simple.asyncapi.yml"
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    val alreadyExistingService = Service(
      id = service.id,
      name = "Random Name",
      version = "1.0.0",
      specifications = Specifications(openapiPath = "simple.openapi.yml"),
      markdown = "Here is my original markdown, please do not override this!",
    )
    val alreadyExistingOpenapiContent = "Some Content"
    catalog.writeService(alreadyExistingService)
    catalog.addFileToService(
      id = alreadyExistingService.id,
      filename = alreadyExistingService.specifications.openapiPath,
      content = alreadyExistingOpenapiContent
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      specifications shouldNotBeNull {
        openapiPath shouldBe alreadyExistingService.specifications.openapiPath
        asyncapiPath shouldBe asyncapiSpecificationFile
        "${catalog.directory}/services/${service.id}/$openapiPath".let {
          existsSync(it) shouldBe true
          readFileSync(it, utf8) shouldBe alreadyExistingOpenapiContent
        }
        "${catalog.directory}/services/${service.id}/$asyncapiPath".let {
          existsSync(it) shouldBe true
        }
      }
    }
  }

  "if a service is configured with an asyncapi specification and it already exists with an asyncapi specification, the existing asyncapi specification is overwritten" {
    //given
    val domain = DomainProperty(
      id = "testdomain",
      name = "Test Domain",
      version = "1.0.0"
    )
    val oldSpec = "simple.asyncapi.yml"
    val alreadyExistingService = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample(oldSpec)
    )
    val newSpec = "simple.asyncapi.new.yml"
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample(newSpec)
    )
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(alreadyExistingService), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service), domain),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id, "1.0.0") shouldNotBeNull {
      specifications shouldNotBeNull {
        asyncapiPath shouldBe oldSpec
        "${catalog.directory}/domains/${domain.id}/services/${service.id}/versioned/1.0.0/".let {
          readdirSync(it, utf8).toSet() shouldContainExactly setOf("changelog.mdx", "index.mdx", oldSpec)
        }
      }
    }
    catalog.getService(service.id) shouldNotBeNull {
      version shouldBe "2.0.0"
      specifications shouldNotBeNull {
        asyncapiPath shouldBe newSpec
        "${catalog.directory}/domains/${domain.id}/services/${service.id}".let {
          readdirSync(it, utf8).toSet() shouldContainExactly setOf("changelog.mdx", "index.mdx", newSpec, "versioned", "commands", "events", "queries")
        }
      }
    }
  }

  "if a service is configured with an asyncapi specification and it already exists with `sends` messages, they are persisted." {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    catalog.writeService(
      Service(
        id = service.id,
        version = "1.0.0",
        sends = mutableListOf(
          SendsPointer("messageToBePersisted", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0")))
        )
      )
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      sends shouldContainExactly setOf(
        SendsPointer("messageToBePersisted", "1.0.0", mutableListOf(ResourcePointer(id="userSignedup", version="1.0.0"))),
        SendsPointer("usersignedup", "1.0.0", mutableListOf(ResourcePointer(id="userSignedup", version="1.0.0"))),
        SendsPointer("usersignedout", "1.0.0", mutableListOf(ResourcePointer(id="userSignedup", version="1.0.0"))),
      )
    }
  }

  "if a service is configured with an asyncapi specification and it already exists with `receives` messages, they are overwritten." {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    catalog.writeService(
      Service(
        id = service.id,
        version = "1.0.0",
        receives = mutableListOf(
          ReceivesPointer("messageToBeOverwritten", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0")))
        )
      )
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      receives shouldContainExactly setOf(
        ReceivesPointer("signupuser", "2.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("getuserbyemail", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("checkemailavailability", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("usersubscribed", "1.0.0", mutableListOf(ResourcePointer("userSubscription", "1.0.0")))
      )
    }
  }

  "if a service is configured with an asyncapi V2 specification, its `publish` operation messages are written as `sends` messages to the service" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi-v2.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      sends shouldContainExactly setOf(
        SendsPointer("somecoolpublishedmessage", "1.0.0", mutableListOf(ResourcePointer("chat", "1.0.0")))
      )
    }
  }

  "if a service is configured with an asyncapi V2 specification, its `subscribe` operation messages are written as `receives` messages to the service" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi-v2.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      receives shouldContainExactly setOf(
        ReceivesPointer("somecoolreceivedmessage", "1.0.0", mutableListOf(ResourcePointer("chat", "1.0.0")))
      )
    }
  }

  "if a service is configured with an asyncapi V3 specification, its `send` operation messages are written as `sends` messages to the service" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      sends shouldContainExactly setOf(
        SendsPointer("usersignedup", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        SendsPointer("usersignedout", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
      )
    }
  }

  "if a service is configured with an asyncapi V3 specification, its `receive` operation messages are written as `sends` messages to the service" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      receives shouldContainExactly setOf(
        ReceivesPointer("signupuser", "2.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("getuserbyemail", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("checkemailavailability", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("usersubscribed", "1.0.0", mutableListOf(ResourcePointer("userSubscription", "1.0.0")))
      )
    }
  }

  "if a service is configured with an asyncapi specification with \$ref schemas, they are resolved and written to the service " {
    //given
    val service = ServiceProperty(
      id = "test-service",
      asyncapiPath = getAsyncapiExample("ref-example.asyncapi.yml"),
    )
    val refMessageInAsyncapiFile = "usersignup"

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBe null
    catalog.getMessage(refMessageInAsyncapiFile) shouldNotBeNull {
      schemaPath shouldBe "schema.json"
    }
  }

  "if a service in configured with an asyncapi specification as a JSON file, it is written to the service " {
    //given
    val service = ServiceProperty(
      id = "user-service",
      asyncapiPath = getAsyncapiExample("example-as-json.json")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      name shouldBe "User Service"
      version shouldBe "1.0.0"
      summary shouldBe "CRUD based API to handle User interactions for users of Kitchenshelf app."
    }
  }

  "if a service is configured with a URL as the specification path, the specification file is downloaded and written to the service" {
    //given
    val openapiSpecificationFile = "petstore.yml"
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = "https://raw.githubusercontent.com/fusion-powered-io/api-generator/refs/heads/main/src/jsTest/resources/openapi/$openapiSpecificationFile",
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      specifications.openapiPath shouldBe openapiSpecificationFile
    }
    existsSync("${catalog.directory}/services/${service.id}/$openapiSpecificationFile") shouldBe true
  }

  "if a service is configured and it already exists with a different version, a new service is created and the old one is versioned" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    val versionInAsyncapiFile = "1.0.0"
    val versionOfExistingService = "0.0.1"
    catalog.writeService(
      Service(
        id = service.id,
        version = versionOfExistingService
      )
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id, versionOfExistingService) shouldNotBe null
    catalog.getService(service.id, versionInAsyncapiFile) shouldNotBe null
  }

  "if a service is configured and its version already exists, only metadata is updated" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml")
    )
    val versionInAsyncapiFile = "1.0.0"
    catalog.writeService(
      Service(
        id = service.id,
        name = "Random Name",
        version = versionInAsyncapiFile
      )
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      id shouldBe service.id
      name shouldBe "Account Service"
      version shouldBe versionInAsyncapiFile
      summary shouldBe "This service is in charge of processing user signups"
      badges shouldContainExactly setOf(
        Badge("Events", "blue", "blue"),
        Badge("Authentication", "blue", "blue")
      )
    }
  }

  "if a service is configured and its version already exists, owners, repository and markdown are persisted" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore-2",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val versionInOpenapiFile = "1.0.0"
    val alreadyExistingService = Service(
      id = service.id,
      name = "Random Name",
      version = versionInOpenapiFile,
      owners = setOf("jbrandao"),
      repository = Repository("kotlin", "https://random.url"),
      markdown = "Here is my original markdown, please do not override this!"
    )
    catalog.writeService(alreadyExistingService)

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      id shouldBe service.id
      name shouldBe "Swagger Petstore"
      version shouldBe versionInOpenapiFile
      summary shouldBe "This is a sample server Petstore server."
      markdown shouldBe alreadyExistingService.markdown
      owners shouldContainAll alreadyExistingService.owners
      repository shouldBe alreadyExistingService.repository
      badges shouldContainExactly setOf(Badge("Pets", "blue", "blue"))
    }
  }

  "if a service is configured and its version already exists, the openapi `sends` messages are persisted" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val alreadyExistingService = Service(
      id = service.id,
      name = "Swagger Petstore",
      version = "1.0.0",
      sends = mutableListOf(SendsPointer("usersignedup", "1.0.0")),
    )
    catalog.writeService(alreadyExistingService)

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      sends shouldContainExactly setOf(
        SendsPointer("usersignedup", "1.0.0"),
        SendsPointer("petVaccinated", "1.0.0"),
      )
    }
  }

  "if a service is configured with owners, they are added to the service" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml"),
      owners = arrayOf("John Doe", "Jane Doe")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      owners shouldBe setOf("John Doe", "Jane Doe")
    }
  }

  "if catalog is in a initialized repository, then assign service's editUrl and repository url" {
    //given
    val service = ServiceProperty(
      id = "account-service",
      asyncapiPath = getAsyncapiExample("simple.asyncapi.yml"),
      owners = arrayOf("John Doe", "Jane Doe")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBeNull {
      editUrl shouldBe "https://github.com/fusion-powered-io/api-generator/blob/main/build/js/packages/@fusionpowered/api-generator-test/catalog/services/$id/index.mdx"
      repository.url shouldBe "https://github.com/fusion-powered-io/api-generator"
    }
  }

})
