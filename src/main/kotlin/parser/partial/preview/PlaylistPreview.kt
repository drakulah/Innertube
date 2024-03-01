package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.isTrackCount
import utils.mixedJsonArray
import utils.nullifyIfEmpty

@Serializable
data class PlaylistPreview(
	val title: String,
	val browseId: String,
	val menu: List<Menu>,
	val trackCount: String?,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parsePlaylistPreview(obj: JsonElement?): PlaylistPreview? {

	val uploaders = arrayListOf<Uploader>()

	val title = (obj.path("title.runs[0].text")
		?: obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
			).maybeStringVal?.nullifyIfEmpty() ?: return null

	val browseId = ChunkParser.parseId(
		obj.path("title.runs[0].navigationEndpoint") ?: obj.path("navigationEndpoint")
	) ?: return null

	val menu = ChunkParser.parseMenu(obj.path("menu"))
	val thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	var trackCount: String? = null

	mixedJsonArray(
		obj.path("subtitle.runs"),
		obj.path("secondTitle.runs"),
		obj.path("flexColumns[1].musicResponsiveListItemFlexColumnRenderer.text.runs")
	).forEach {
		val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))
		val tempText = it.path("text")?.maybeStringVal?.nullifyIfEmpty() ?: return@forEach

		when (tempType) {
			ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
				uploaders.add(
					Uploader(
						title = tempText,
						isArtist = tempType == ItemType.ArtistPreview,
						browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
					)
				)
			}

			else -> {
				if (tempText.isTrackCount()) trackCount = tempText
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