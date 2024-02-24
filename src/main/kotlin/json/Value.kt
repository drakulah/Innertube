package json

import kotlinx.serialization.json.*

val JsonElement?.maybeStringVal get(): String? = this?.jsonPrimitive?.content
val JsonElement?.maybeIntVal get(): Int? = this?.jsonPrimitive?.intOrNull
val JsonElement?.maybeLongVal get(): Long? = this?.jsonPrimitive?.longOrNull
val JsonElement?.maybeFloatVal get(): Float? = this?.jsonPrimitive?.floatOrNull
val JsonElement?.maybeBoolVal get(): Boolean? = this?.jsonPrimitive?.booleanOrNull