package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkMessage
import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkQuery
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Message
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Sidebar

fun SdkMessage.toMessage() =
  Message(
    id = id,
    name = name,
    version = version,
    summary = summary,
    schemaPath = schemaPath,
    badges = badges?.map { it.toBadge() }?.toSet() ?: emptySet(),
    sidebar = sidebar?.toSidebar() ?: Sidebar(),
    owners = owners?.toSet() ?: emptySet(),
    markdown = markdown,
    editUrl = editUrl,
  )

fun Message.toSdkMessage() =
  SdkMessage(
    id = id,
    name = name,
    version = version,
    summary = summary,
    schemaPath = schemaPath,
    badges = badges.map { it.toSdkBadge() }.toSet().toTypedArray(),
    sidebar = sidebar.toSdkSidebar(),
    owners = owners.toTypedArray(),
    markdown = markdown,
    editUrl = editUrl,
  )

fun Message.toSdkQuery() =
  SdkQuery(
    id = id,
    name = name,
    version = version,
    summary = summary,
    schemaPath = schemaPath,
    badges = badges.map { it.toSdkBadge() }.toSet().toTypedArray(),
    sidebar = sidebar.toSdkSidebar(),
    owners = owners.toTypedArray(),
    markdown = markdown,
    editUrl = editUrl,
  )