package io.fusionpowered.eventcatalog.apigenerator.integration

import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.Properties
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.model.ServiceProperty
import io.fusionpowered.eventcatalog.apigenerator.adapter.primary.plugin.plugin
import io.fusionpowered.eventcatalog.apigenerator.application.ApiGeneratorService
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getAsyncapiExample
import io.fusionpowered.eventcatalog.apigenerator.configuration.ApiGeneratorTestConfig.Companion.getOpenapiExample
import io.fusionpowered.eventcatalog.apigenerator.extensions.CatalogExtension.catalog
import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData.Message.Type.Command
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Badge
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Message
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ReceivesPointer
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.ResourcePointer
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.SendsPointer
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Service
import io.kotest.core.spec.style.StringSpec
import io.kotest.matchers.collections.shouldContainExactly
import io.kotest.matchers.nulls.shouldNotBeNull
import io.kotest.matchers.shouldBe
import io.kotest.matchers.shouldNotBe
import io.kotest.matchers.string.shouldContain
import io.kotest.matchers.string.shouldNotContain
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.await
import kotlinx.coroutines.promise
import node.buffer.BufferEncoding.Companion.utf8
import node.fs.existsSync
import node.fs.readFileSync


class Message : StringSpec({

  "if a message is defined in an openapi specification without `x-eventcatalog-message-type`, it is added to the service as a query" {
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
    catalog.getMessage("list-pets") shouldNotBeNull {
      id shouldBe "list-pets"
      name shouldBe "List Pets"
      version shouldBe "5.0.0"
      summary shouldBe "List all pets"
      badges shouldContainExactly setOf(
        Badge("GET", "blue", "blue"),
        Badge("tag:pets", "blue", "blue"),
      )
    }
  }

  "when the message already exists in EventCatalog but the versions do not match, the existing message is versioned" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val alreadyExistingMessage = Message(
      id = "createPets",
      name = "createPets",
      version = "0.0.1",
    )
    catalog.writeMessage(
      message = alreadyExistingMessage,
      type = Command,
      service = Service(
        id = service.id,
        version = "1.0.0"
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
    catalog.getMessage(alreadyExistingMessage.id, alreadyExistingMessage.version) shouldNotBe null
    catalog.getMessage("createPets", "1.0.0") shouldNotBe null
  }

  "when a the message already exists in EventCatalog the markdown is persisted" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val alreadyExistingMessage = Message(
      id = "createPets",
      name = "createPets",
      version = "0.0.1",
      markdown = "Not to be overwritten by the generator"
    )
    catalog.writeMessage(
      message = alreadyExistingMessage,
      type = Command,
      service = Service(
        id = service.id,
        version = "1.0.0"
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
    catalog.getMessage(alreadyExistingMessage.id) shouldNotBeNull {
      markdown shouldBe alreadyExistingMessage.markdown
    }
  }

  "when the message (operation) does not have a operationId, the path and status code is used to uniquely identify the message" {
    //given
    val service = ServiceProperty(
      id = "product-api",
      openapiPath = getOpenapiExample("without-operationIds.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getMessage("product-api_GET_{productId}") shouldNotBe null
    catalog.getMessage("product-api_GET") shouldNotBe null
  }

  "when the service has owners, the messages are given the same owners" {
    //given
    val service = ServiceProperty(
      id = "product-api",
      openapiPath = getOpenapiExample("without-operationIds.yml"),
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
    catalog.getMessage("product-api_GET_{productId}") shouldNotBeNull {
      owners shouldContainExactly service.owners!!.toSet()
    }
    catalog.getMessage("product-api_GET") shouldNotBeNull {
      owners shouldContainExactly service.owners!!.toSet()
    }
  }

  "when a message already exists in EventCatalog with the same version the metadata is updated" {
    //given
    val service = ServiceProperty(
      id = "swagger-petstore",
      openapiPath = getOpenapiExample("petstore.yml")
    )
    val alreadyExistingMessage = Message(
      id = "createPets",
      name = "Random Name value",
      version = "0.0.1",
      markdown = "Not to be overwritten by the generator"
    )
    catalog.writeMessage(
      message = alreadyExistingMessage,
      type = Command,
      service = Service(
        id = service.id,
        version = "1.0.0"
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
    catalog.getMessage(alreadyExistingMessage.id) shouldNotBeNull {
      name shouldBe "createPets"
    }
  }

  "messages defined using the custom `x-eventcatalog-message-type` header in an OpenAPI are documented in EventCatalog accordingly" {
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
    existsSync("${catalog.directory}/services/${service.id}/queries/showPetById") shouldBe true
    existsSync("${catalog.directory}/services/${service.id}/events/petAdopted") shouldBe true
    existsSync("${catalog.directory}/services/${service.id}/commands/createPets") shouldBe true
  }

  "messages marked as `sends` using the custom `x-eventcatalog-message-action` header in an OpenAPI are mapped against the service as messages the service sends" {
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
      sends shouldContainExactly listOf(SendsPointer("petVaccinated", "1.0.0"))
    }
  }

  "when messages have the `x-eventcatalog-message-name` extension defined, this value is used for the message name" {
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
    catalog.getMessage("list-pets") shouldNotBeNull {
      name shouldBe "List Pets"
    }
  }

  "when messages have the `x-eventcatalog-message-id` extension defined, this value is used for the message id" {
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
    catalog.getMessage("list-pets") shouldNotBeNull {
      id shouldBe "list-pets"
    }
  }

  "when messages have the `x-eventcatalog-message-version` extension defined, this value is used for the message version" {
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
    catalog.getMessage("list-pets") shouldNotBeNull {
      version shouldBe "5.0.0"
    }
  }

  "when a message has a request body, the request body is the schema of the message" {
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
    catalog.getMessage("createPets") shouldNotBeNull {
      schemaPath shouldBe "request-body.json"
      readFileSync("${catalog.directory}/services/${service.id}/commands/$id/$schemaPath", utf8) shouldNotBe null
    }
  }

  "when a message has a request body, the markdown contains the request body" {
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
    catalog.getMessage("createPets") shouldNotBeNull {
      markdown shouldContain """
        ## POST(/pets)

        ### Body
        <SchemaViewer file="request-body.json" maxHeight="500" id="request-body" />
      """.trimIndent()
    }
  }

  "when a message has a response, the response is stored as a schema against the message" {
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
    catalog.getMessage("createPets") shouldNotBeNull {
      readFileSync("${catalog.directory}/services/${service.id}/commands/$id/response-default.json", utf8) shouldNotBe null
    }
  }

  "when a message has a response, the response is shown in the markdown file" {
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
    catalog.getMessage("createPets") shouldNotBeNull {
      markdown shouldContain """
        ### Responses

        #### <span className="text-green-500">201 Created</span>

        #### <span className="text-gray-500">default</span>
        <SchemaViewer file="response-default.json" maxHeight="500" id="response-default" />
      """.trimIndent()
    }
  }

  "when a message has parameters they are added to the markdown file when the message is new in the catalog" {
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
    catalog.getMessage("list-pets") shouldNotBeNull {
      markdown shouldContain """
        ### Parameters
        - **limit** (query): How many items to return at one time (max 100)
      """.trimIndent()
    }
  }

  "when a message has circular references, the plugin adds [Circular] to the schema" {
    val service = ServiceProperty(
      id = "circular-ref-service",
      openapiPath = getOpenapiExample("circular-ref.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getMessage("employees-api_GET_employees") shouldNotBeNull {
      readFileSync("${catalog.directory}/services/${service.id}/queries/$id/response-200.json", utf8) shouldBe """
        {
          "type": "array",
          "items": {
            "type": "object",
            "properties": {
              "id": {
                "type": "string"
              },
              "name": {
                "type": "string"
              },
              "manager": "[Circular]"
            }
          }
        }
      """.trimIndent()
    }
  }

  "messages defined in an asyncapi specification that do not have an eventcatalog header are documented as events by default in EventCatalog" {
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
    existsSync("${catalog.directory}/services/${service.id}/events/usersignedout") shouldBe true
  }

  "when the `x-eventcatalog-role` is defined and set to `client` the generator does not create or modify the message documentation, but still included in the service (sends/receives)" {
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
    catalog.getMessage("usersubscribed") shouldBe null
    catalog.getService(service.id) shouldNotBeNull {
      receives shouldContainExactly listOf(
        ReceivesPointer("signupuser", "2.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("getuserbyemail", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("checkemailavailability", "1.0.0", mutableListOf(ResourcePointer("userSignedup", "1.0.0"))),
        ReceivesPointer("usersubscribed", "1.0.0", mutableListOf(ResourcePointer("userSubscription", "1.0.0")))
      )
    }
  }

  "when a messages has no payload, the message is still parsed and added to the catalog" {
    //given
    val service = ServiceProperty(
      id = "my-service",
      asyncapiPath = getAsyncapiExample("asyncapi-without-payload.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getMessage("messageprojectdeleted") shouldNotBeNull {
      id shouldBe "messageprojectdeleted"
      version shouldBe "1.0.0"
      name shouldBe "Message.ProjectDeleted"
      summary shouldBe "Message summary"
      badges shouldBe emptySet()
      markdown shouldBe """
        ## Overview
        Event description
        
        ## Architecture
        
        <NodeGraph />
      """.trimIndent()
    }
  }

  "when a message has a schema defined in the AsyncAPI file, the schema is documented in EventCatalog" {
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
    catalog.getMessage("usersignedup") shouldNotBeNull {
      schemaPath shouldBe "schema.json"
      readFileSync("${catalog.directory}/services/${service.id}/events/$id/schema.json", utf8) shouldNotBe null
    }
  }

  "parses the AsyncAPI file with avro schemas, and stores the avro schema against the event" {
    //given
    val service = ServiceProperty(
      id = "user-signup-api",
      asyncapiPath = getAsyncapiExample("asyncapi-with-avro.asyncapi.yml")
    )

    //when
    GlobalScope.promise {
      plugin(
        pluginConfig = Properties(arrayOf(service)),
        generator = ApiGeneratorService(catalog)
      )
    }.await()

    //then
    catalog.getService(service.id) shouldNotBe null
    catalog.getMessage("usersignedup") shouldNotBeNull {
      schemaPath shouldBe "schema.avsc"
      readFileSync("${catalog.directory}/services/${service.id}/events/$id/$schemaPath", utf8) shouldNotBe """
        {
            type: 'record',
            name: 'UserSignedUp',
            namespace: 'com.company',
            doc: 'User sign-up information',
            fields: [
                {
                    name: 'userId',
                    type: 'int',
                },
                {
                    name: 'userEmail',
                    type: 'string',
                },
            ],
        }
      """.trimIndent()
    }
  }

  "if a message has markdown with curly brackets, they should be escaped" {
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
    catalog.getMessage("petAdopted") shouldNotBeNull {
      var nonEscapedCurlyBrackets = Regex("(?<!\\\\)[{}]")
      markdown shouldNotContain nonEscapedCurlyBrackets
    }
  }

  "if catalog is in a initialized repository, then assign the message url to the editUrl" {
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
    catalog.getMessage("petAdopted") shouldNotBeNull {
      editUrl shouldBe "https://github.com/fusion-powered-io/api-generator/blob/main/build/js/packages/@fusionpowered/api-generator-test/catalog/services/${service.id}/events/$id/index.mdx"
    }
  }

})
