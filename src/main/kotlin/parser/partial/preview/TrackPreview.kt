package parser.partial.preview

import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonObject
import parser.partial.chunk.*
import utils.removeEmpty

@Serializable
data class TrackPreview(
	val id: String,
	val type: String,
	val title: List<RunsText>,
	val subtitle: List<RunsText>,
	val menu: List<Menu>,
	val thumbnails: List<ThumbnailInfo>
): PreviewParser.ContentPreview()

fun PreviewParser.parseTrackPreview(obj: JsonElement?): TrackPreview? {
	return obj?.path("musicTwoColumnItemRenderer")?.jsonObject?.let {
		val thumbnails = ChunkParser.parseThumbnail(it["thumbnail"])
		val title = ChunkParser.parseRunsText(it.path("title.runs")).removeEmpty()
		val subtitle = ChunkParser.parseRunsText(it.path("subtitle.runs")).removeEmpty()
		val navEndpoint = ChunkParser.parseNavEndpoint(it.path("navigationEndpoint"))
		val menu = ChunkParser.parseMenu(it.path("menu"))

		if (title.isEmpty()
			|| navEndpoint?.id?.isEmpty() == true
			|| navEndpoint?.type?.isEmpty() == true
		) return@let null

		return@let TrackPreview(
			id = navEndpoint?.id!!, type = navEndpoint.type, title, subtitle, menu, thumbnails
		)
	}
}