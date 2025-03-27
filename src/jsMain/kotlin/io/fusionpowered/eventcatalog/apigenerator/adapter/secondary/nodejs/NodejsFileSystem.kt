package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs

import io.fusionpowered.eventcatalog.apigenerator.port.FileSystem
import node.buffer.BufferEncoding
import node.fs.readFileSync

object NodejsFileSystem : FileSystem {

  override fun readFile(path: String): String? {
    return readFileSync(path, BufferEncoding.utf8)
  }

}