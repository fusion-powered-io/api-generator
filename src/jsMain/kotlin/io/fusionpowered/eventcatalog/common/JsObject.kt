package io.fusionpowered.eventcatalog.common

/**
 * The sdk requires that unused specs not exist as a property in the object
 * So we need this hack to clear them.
 * When we do that, we lose the notion of the type we once had, so we can only return a dynamic
 */
fun Any.asDynamicWithNoEmptyProperties(): dynamic {
  val jsObject = this.asDynamic()
  js(
    """
       for (var key in jsObject) {
         if (jsObject[key].length === 0) {
             delete jsObject[key];
          }
       };
    """
  )
  return jsObject
}