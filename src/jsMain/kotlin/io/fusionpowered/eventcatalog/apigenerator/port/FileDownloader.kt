package io.fusionpowered.eventcatalog.apigenerator.port

interface FileDownloader {

  suspend fun download(url: String): String?

}