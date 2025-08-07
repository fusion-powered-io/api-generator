package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.nodejs

import io.fusionpowered.eventcatalog.apigenerator.port.RepositoryConfig
import js.objects.unsafeJso
import node.buffer.BufferEncoding
import node.childProcess.ExecSyncOptionsWithStringEncoding
import node.childProcess.execSync

object NodejsGitRepositoryConfig : RepositoryConfig {

  private val isInitialized: Boolean =
    execSync(
      "git rev-parse --is-inside-work-tree",
      unsafeJso<ExecSyncOptionsWithStringEncoding> { stringEncoding = BufferEncoding.utf8 }
    )
      .replace("\n", "")
      .trim()
      .equals("true", ignoreCase = true)

  override val remoteUrl: String =
    when (isInitialized) {
      true -> execSync(
        "git remote -v",
        unsafeJso<ExecSyncOptionsWithStringEncoding> { stringEncoding = BufferEncoding.utf8 }
      )

        .split(" ", "\t")[1]
        .replace(".git", "")

      false -> ""
    }

  override val topLevelDirectory: String =
    execSync(
      "git rev-parse --show-toplevel",
      unsafeJso<ExecSyncOptionsWithStringEncoding> { stringEncoding = BufferEncoding.utf8 }
    )
      .replace("\n", "")
      .trim()

  override val defaultBranch: String =
    execSync(
      " git remote show origin | sed -n '/HEAD branch/s/.*: //p'",
      unsafeJso<ExecSyncOptionsWithStringEncoding> { stringEncoding = BufferEncoding.utf8 }
    )
      .replace("\n", "")
      .trim()

}