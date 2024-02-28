package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.*

@Serializable
enum class AlbumType {
	Single,
	Episode,
	Album
}

fun String?.toAlbumType(): AlbumType {
	return when (this?.uppercase()) {
		"EP" -> AlbumType.Episode
		"SINGLE" -> AlbumType.Single
		else -> AlbumType.Album
	}
}

@Serializable
data class AlbumBasicInfo(
	val title: String,
	val browseId: String?
)

@Serializable
data class AlbumPreview(
	val year: String?,
	val title: String,
	val browseId: String,
	val albumType: AlbumType,
	val menu: List<Menu>,
	val uploaders: List<Uploader>,
	val thumbnails: List<ThumbnailInfo>
): PreviewParser.ContentPreview()

fun PreviewParser.parseAlbumPreview(obj: JsonElement?): AlbumPreview? {
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

	var albumType = AlbumType.Album
	var yearText: String? = null

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
				when {
					t_text.isYearText() -> yearText = t_text
					t_text.isAlbumType() -> albumType = t_text.toAlbumType()
				}
			}
		}
	}

	return AlbumPreview(
		year = yearText,
		title = title,
		browseId = browseId,
		albumType = albumType,
		menu = menu,
		uploaders = uploaders,
		thumbnails = thumbnails
	)
}