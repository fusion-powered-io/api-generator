package io.fusionpowered.eventcatalog.apigenerator.model.catalog

import io.fusionpowered.eventcatalog.apigenerator.model.import.DomainImportData

data class Domain(
  val id: String,
  val name: String,
  val version: String,
  val summary: String = "",
  val services: MutableList<ResourcePointer> = mutableListOf(),
  val entities: Set<String> = emptySet(),
  val owners: Set<String> = emptySet(),
  val markdown: String = DEFAULT_MARKDOWN,
) {

  companion object {

    private const val DEFAULT_MARKDOWN = "## Architecture diagram\n<NodeGraph />\n\n"

  }

  internal constructor(importData: DomainImportData) : this(
    id = importData.id,
    name = importData.name,
    version = importData.version,
    markdown = DEFAULT_MARKDOWN,
    owners = importData.owners,
  )

}
