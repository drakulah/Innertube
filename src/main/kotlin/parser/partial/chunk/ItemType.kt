package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
enum class ItemType {
	Song,
	Video,
	AlbumPreview,
	ArtistPreview,
	PlaylistPreview,
	UserChannelPreview
}

/**
 * Provide __Object.navigationEndpoint__
 */
fun ChunkParser.parseItemType(obj: JsonElement?): ItemType? {
	return when ((obj.path("browseEndpoint.browseEndpointContextSupportedConfigs.browseEndpointContextMusicConfig.pageType")
		?: obj.path("watchEndpoint.watchEndpointMusicSupportedConfigs.watchEndpointMusicConfig.musicVideoType"))
		?.maybeStringVal) {
		"MUSIC_VIDEO_TYPE_ATV" -> ItemType.Song
		"MUSIC_PAGE_TYPE_ALBUM" -> ItemType.AlbumPreview
		"MUSIC_PAGE_TYPE_ARTIST" -> ItemType.ArtistPreview
		"MUSIC_PAGE_TYPE_USER_CHANNEL" -> ItemType.UserChannelPreview
		"MUSIC_PAGE_TYPE_PLAYLIST" -> ItemType.PlaylistPreview
		"MUSIC_VIDEO_TYPE_OMV", "MUSIC_VIDEO_TYPE_UGC" -> ItemType.Video
		else -> null
	}
}