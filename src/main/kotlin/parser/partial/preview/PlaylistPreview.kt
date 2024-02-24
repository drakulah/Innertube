package parser.partial.preview

import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.mix
import utils.removeEmpty

@Serializable
data class PlaylistPreview(
	val title: List<RunsText>,
	val subtitle: List<RunsText>,
	val browseId: String,
	val menu: List<Menu>,
	val thumbnails: List<ThumbnailInfo>
): PreviewParser.ContentPreview()

fun PreviewParser.parsePlaylistPreview(obj: JsonElement?): PlaylistPreview? {

	val navEndpoint = ChunkParser.parseNavEndpoint(
		obj.path("title.runs[0].navigationEndpoint") ?:
		obj.path("navigationEndpoint")
	)

	val title = ChunkParser.parseRunsText(
		obj.path("title.runs") ?:
		obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs")
	).removeEmpty()

	val subtitle = ChunkParser.parseRunsText(obj.path("subtitle.runs"))
		.mix(ChunkParser.parseRunsText(obj.path("secondTitle.runs")))
		.mix(ChunkParser.parseRunsText(obj.path("flexColumns[1].musicResponsiveListItemFlexColumnRenderer.text.runs")))
		.removeEmpty()

	val browseId = navEndpoint?.id

	val thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	val menu = ChunkParser.parseMenu(obj.path("menu"))

	if (title.isEmpty()
		|| browseId.isNullOrEmpty()
	) return null

	return PlaylistPreview(
		title,
		subtitle,
		browseId,
		menu,
		thumbnails
	)
}