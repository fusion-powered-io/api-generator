package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkSidebar
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Sidebar

fun SdkSidebar.toSidebar() =
  Sidebar(
    badge = badge
  )

fun Sidebar.toSdkSidebar() =
  SdkSidebar(
    badge = badge
  )