package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.isAlbumType
import utils.isYearText
import utils.mixedJsonArray
import utils.nullifyIfEmpty

@Serializable
enum class AlbumType {
	Single,
	EP,
	Album
}

fun String?.toAlbumType(): AlbumType {
	return when (this?.uppercase()) {
		"EP" -> AlbumType.EP
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
) : PreviewParser.ContentPreview()

fun PreviewParser.parseAlbumPreview(obj: JsonElement?): AlbumPreview? {
	val uploaders = arrayListOf<Uploader>()

	val title = (obj.path("title.runs[0].text")
		?: obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
			).maybeStringVal?.nullifyIfEmpty() ?: return null

	val browseId = ChunkParser.parseId(
		obj.path("title.runs[0].navigationEndpoint") ?: obj.path("navigationEndpoint")
	) ?: return null

	val menu = ChunkParser.parseMenu(obj.path("menu"))
	val thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	var albumType = AlbumType.Album
	var yearText: String? = null

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
				when {
					tempText.isYearText() -> yearText = tempText
					tempText.isAlbumType() -> albumType = tempText.toAlbumType()
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