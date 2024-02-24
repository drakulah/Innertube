package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

fun ChunkParser.parseContinuation(obj: JsonElement?): String? {
	return (obj?.path("nextContinuationData.continuation")
		?: obj?.path("nextRadioContinuationData.continuation"))?.maybeStringVal
}