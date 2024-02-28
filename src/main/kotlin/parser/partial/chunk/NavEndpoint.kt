package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

@Serializable
data class NavEndpoint(
	val id: String,
	val type: String
)

/**
 * Provide __Object.navigationEndpoint__
 */
fun ChunkParser.parseNavEndpoint(obj: JsonElement?): NavEndpoint? {
	return obj.path("watchEndpoint")?.let {
		NavEndpoint(
			id = obj.path("videoId")?.maybeStringVal ?: return null,
			type = obj.path("watchEndpointMusicSupportedConfigs.watchEndpointMusicConfig.musicVideoType")?.maybeStringVal ?: return null
		)
	} ?: obj.path("browseEndpoint")?.let {
		NavEndpoint(
			id = obj.path("browseId")?.maybeStringVal ?: return null,
			type = obj.path("browseEndpointContextSupportedConfigs.browseEndpointContextMusicConfig.pageType")?.maybeStringVal ?: return null
		)
	}
}