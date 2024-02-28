package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.*

@Serializable
data class Uploader(
	val title: String,
	val isArtist: Boolean,
	val browseId: String?
)

@Serializable
data class ArtistPreview(
	val title: String,
	val browseId: String,
	val subscriberCount: String?,
	val menu: List<Menu>,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parseArtistPreview(obj: JsonElement?): ArtistPreview? {

	val navEndpoint = ChunkParser.parseNavEndpoint(
		obj.path("title.runs[0].navigationEndpoint") ?: obj.path("navigationEndpoint")
	)

	val title = (obj.path("title.runs[0].text")
		?: obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
	).maybeStringVal?.nullifyIfEmpty() ?: return null

	var subscribersCount: String? = null
	val browseId = navEndpoint?.id?.nullifyIfEmpty() ?: return null
	val menu = ChunkParser.parseMenu(obj.path("menu"))
	val thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	mixedJsonArray(
		obj.path("subtitle.runs"),
		obj.path("secondTitle.runs"),
		obj.path("flexColumns[1].musicResponsiveListItemFlexColumnRenderer.text.runs")
	).forEach {
		val t_text = it.path("text")?.maybeStringVal?.trim() ?: return@forEach
		if (t_text.isSubscriberCount()) subscribersCount = t_text
	}

	return ArtistPreview(
		title = title,
		subscriberCount = subscribersCount,
		browseId = browseId,
		menu = menu,
		thumbnails = thumbnails
	)
}