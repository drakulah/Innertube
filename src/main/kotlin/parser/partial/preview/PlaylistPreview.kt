package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.*

@Serializable
data class PlaylistPreview(
	val title: String,
	val browseId: String,
	val menu: List<Menu>,
	val trackCount: String?,
	val thumbnails: List<ThumbnailInfo>
): PreviewParser.ContentPreview()

fun PreviewParser.parsePlaylistPreview(obj: JsonElement?): PlaylistPreview? {

	val uploaders = arrayListOf<Uploader>()

	val navEndpoint = ChunkParser.parseNavEndpoint(
		obj.path("title.runs[0].navigationEndpoint") ?:
		obj.path("navigationEndpoint")
	)

	val title = (obj.path("title.runs[0].text") ?:
		obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
	).maybeStringVal?.nullifyIfEmpty() ?: return null

	val browseId = navEndpoint?.id?.nullifyIfEmpty() ?: return null
	val menu = ChunkParser.parseMenu(obj.path("menu"))
	val thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	var trackCount: String? = null

	mixedJsonArray(
		obj.path("subtitle.runs"),
		obj.path("secondTitle.runs"),
		obj.path("flexColumns[1].musicResponsiveListItemFlexColumnRenderer.text.runs")
	).forEach {
		val t_type = ChunkParser.parseItemType(it.path("navigationEndpoint"))
		val t_text = it.path("text")?.maybeStringVal?.trim() ?: return@forEach

		when (t_type) {
			ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
				if (t_text.isEmpty()) return@forEach
				uploaders.add(
					Uploader(
						title = t_text,
						isArtist = t_type == ItemType.ArtistPreview,
						browseId = ChunkParser.parseNavEndpoint(it)?.id
					)
				)
			}
			else -> {
				if (t_text.isTrackCount()) trackCount = t_text
			}
		}
	}

	return PlaylistPreview(
		title = title,
		browseId = browseId,
		trackCount = trackCount,
		menu = menu,
		thumbnails = thumbnails
	)
}