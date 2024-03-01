package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.json.JsonElement

/**
 * Provide __Object.continuation__
 */
fun ChunkParser.parseContinuation(obj: JsonElement?): String? {
	return (obj?.path("nextContinuationData.continuation")
		?: obj?.path("nextRadioContinuationData.continuation"))?.maybeStringVal
}