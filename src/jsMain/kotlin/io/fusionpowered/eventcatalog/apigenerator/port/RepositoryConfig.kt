package io.fusionpowered.eventcatalog.apigenerator.port

interface RepositoryConfig {

  val remoteUrl: String

  val topLevelDirectory: String

  val defaultBranch: String

}