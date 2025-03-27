package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.mapper

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.eventcatalog.model.SdkBadge
import io.fusionpowered.eventcatalog.apigenerator.model.catalog.Badge

fun SdkBadge.toBadge() =
  Badge(
    content = content,
    backgroundColor = backgroundColor,
    textColor = textColor
  )

fun Badge.toSdkBadge() =
  SdkBadge(
    content = content,
    backgroundColor = backgroundColor,
    textColor = textColor
  )