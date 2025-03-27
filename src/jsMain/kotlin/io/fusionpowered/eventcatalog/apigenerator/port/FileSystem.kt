package io.fusionpowered.eventcatalog.apigenerator.port

interface FileSystem {

  fun readFile(path: String): String?

}