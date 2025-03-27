package io.fusionpowered.eventcatalog.apigenerator.adapter.secondary.swaggerparser.jsmodule

import js.objects.unsafeJso

@JsModule("slugify")
@JsNonModule
private external fun jsSlugify(word: String, options: dynamic): String

fun slugify(text: String) =
  jsSlugify(text.lowercase(), unsafeJso { lower = true })