package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs

import io.fusionpowered.eventcatalog.apigenerator.port.FileDownloader
import web.http.fetch

object NodejsFileDownloader : FileDownloader {

  override suspend fun download(url: String): String? {
    return fetch(url).text()
  }

}