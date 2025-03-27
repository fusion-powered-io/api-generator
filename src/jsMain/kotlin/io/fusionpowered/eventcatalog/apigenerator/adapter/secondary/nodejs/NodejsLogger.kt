package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs

import io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs.jsModule.Chalk
import io.fusionpowered.eventcatalog.apigenerator.port.Logger

object NodejsLogger : Logger {

  override fun debug(message: String) {
    console.log(Chalk.green(message))
  }

  override fun highlightedInfo(message: String) {
    console.info(Chalk.blue(message))
  }

  override fun info(message: String) {
    console.info(Chalk.cyan(message))
  }

  override fun warn(message: String) {
    console.warn(Chalk.yellow(message))
  }

  override fun error(message: String) {
    console.error(Chalk.red(message))
  }

}