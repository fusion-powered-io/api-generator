package io.fusionpowered.eventcatalog.apigenerator.model.catalog

import io.fusionpowered.eventcatalog.apigenerator.model.api.ApiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.AsyncapiData
import io.fusionpowered.eventcatalog.apigenerator.model.api.OpenapiData
import io.fusionpowered.eventcatalog.apigenerator.model.import.ServiceImportData

data class Service(
  val id: String,
  val version: String,
  val name: String = "",
  val summary: String = "",
  val schemaPath: String = "",
  val badges: Set<Badge> = emptySet(),
  val owners: Set<String> = emptySet(),
  val specifications: Specifications = Specifications(),
  val repository: Repository = Repository(),
  val sends: MutableList<ResourcePointer> = mutableListOf(),
  val receives: MutableList<ResourcePointer> = mutableListOf(),
  val markdown: String = "",
  val editUrl: String = "",
) {

  constructor(
    importData: ServiceImportData,
    openapiServiceApiData: OpenapiData.Service? = null,
    asyncapiServiceData: AsyncapiData.Service? = null
  ) : this(
    id = importData.id,
    name = (openapiServiceApiData ?: asyncapiServiceData!!).name,
    summary = (openapiServiceApiData ?: asyncapiServiceData!!).description.truncate(150),
    version = when {
      openapiServiceApiData != null && asyncapiServiceData != null -> "${openapiServiceApiData.version}-${asyncapiServiceData.version}"
      openapiServiceApiData != null -> openapiServiceApiData.version
      asyncapiServiceData != null -> asyncapiServiceData.version
      else -> throw IllegalStateException("No service data available!")
    },
    badges = when {
      openapiServiceApiData != null && asyncapiServiceData != null -> (openapiServiceApiData.tags + asyncapiServiceData.tags).map { it.toBadge() }.toSet()
      openapiServiceApiData != null -> openapiServiceApiData.tags.map { it.toBadge() }.toSet()
      asyncapiServiceData != null -> asyncapiServiceData.tags.map { it.toBadge() }.toSet()
      else -> throw IllegalStateException("No service data available!")
    },
    owners = importData.owners,
    schemaPath = (openapiServiceApiData ?: asyncapiServiceData!!).schemaPath,
    markdown = (openapiServiceApiData ?: asyncapiServiceData!!).defaultMarkdown,
    specifications = Specifications(
      openapiPath = openapiServiceApiData?.schemaPath ?: "",
      asyncapiPath = asyncapiServiceData?.schemaPath ?: "",
    )
  )

  companion object {

    private fun String.truncate(length: Int) =
      if (length >= this.length) this else substring(0, length - 3) + "..."


    private fun String.toBadge() =
      Badge(
        content = this,
        textColor = "blue",
        backgroundColor = "blue"
      )

    private val ApiData.Service.defaultMarkdown: String
      get() {
        val titleMarkdown = when {
          description.isNotBlank() -> "\n$description\n\n"
          else -> ""
        }
        val externalDocumentationMarkdown = externalDocumentation
          ?.run { "## External documentation\n-[${description}](${url})\n" }
          ?: ""

        return titleMarkdown +
          "## Architecture diagram\n<NodeGraph />\n\n" +
          externalDocumentationMarkdown
      }

  }

}