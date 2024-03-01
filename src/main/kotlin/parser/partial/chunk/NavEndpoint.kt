package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.json.JsonElement
import utils.nullifyIfEmpty

/**
 * Provide __Object.navigationEndpoint__
 */
fun ChunkParser.parseId(obj: JsonElement?): String? {
	return (obj.path("watchEndpoint.videoId").maybeStringVal?.nullifyIfEmpty()
		?: obj.path("browseEndpoint.browseId").maybeStringVal?.nullifyIfEmpty())
}