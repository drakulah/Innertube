package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.*
import utils.*

@Serializable
data class TrackPreview(
	val id: String,
	val title: String,
	val durationText: String?,
	val playCount: String?,
	val album: AlbumBasicInfo?,
	val menu: List<Menu>,
	val uploaders: List<Uploader>,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parseTrackPreview(obj: JsonElement?): TrackPreview? {

	val uploaders = arrayListOf<Uploader>()

	val navEndpoint = ChunkParser.parseNavEndpoint(
		obj.path("title.runs[0].navigationEndpoint") ?:
		obj.path("navigationEndpoint")
	)

	var playCount: String? = null
	var durationText: String? = null
	var album: AlbumBasicInfo? = null
	var title = (obj.path("title.runs[0].text") ?:
	obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
			).maybeStringVal?.nullifyIfEmpty() ?: return null

	var id = navEndpoint?.id?.nullifyIfEmpty() ?: return null
	val menu = ChunkParser.parseMenu(obj.path("menu"))
	val thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnailRenderer") ?: obj.path("thumbnail"))

	mixedJsonArray(
		obj.path("subtitle.runs"),
		obj.path("lengthText.runs"),
		obj.path("secondTitle.runs"),
		obj.path("longBylineText.runs"),
		obj.path("shortBylineText.runs"),
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
			ItemType.Song, ItemType.Video -> {
				title = t_text
				id = ChunkParser.parseNavEndpoint(it.path("navigationEndpoint"))?.id?.nullifyIfEmpty() ?: return@forEach
			}
			ItemType.AlbumPreview -> {
				album = AlbumBasicInfo(
					title = t_text,
					browseId = ChunkParser.parseNavEndpoint(it.path("navigationEndpoint"))?.id?.nullifyIfEmpty()
				)
			}
			else -> {
				when {
					t_text.isDurationText() -> durationText = t_text
					t_text.isTrackPlays() -> playCount = t_text
				}
			}
		}
	}

	mixedJsonArray(
		obj.path("flexColumns"),
		obj.path("fixedColumns")
	).forEach { secObj ->
		mixedJsonArray(
			secObj.path("subtitle.runs"),
			secObj.path("lengthText.runs"),
			secObj.path("secondTitle.runs"),
			secObj.path("longBylineText.runs"),
			secObj.path("shortBylineText.runs"),
			secObj.path("flexColumns[1].musicResponsiveListItemFlexColumnRenderer.text.runs")
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
				ItemType.Song, ItemType.Video -> {
					title = t_text
					id = ChunkParser.parseNavEndpoint(it.path("navigationEndpoint"))?.id?.nullifyIfEmpty() ?: return@forEach
				}
				ItemType.AlbumPreview -> {
					album = AlbumBasicInfo(
						title = t_text,
						browseId = ChunkParser.parseNavEndpoint(it.path("navigationEndpoint"))?.id?.nullifyIfEmpty()
					)
				}
				else -> {
					when {
						t_text.isDurationText() -> durationText = t_text
						t_text.isTrackPlays() -> playCount = t_text
					}
				}
			}
		}
	}

	return TrackPreview(
		id = id,
		title = title,
		playCount = playCount,
		durationText = durationText,
		album = album,
		menu = menu,
		uploaders = uploaders,
		thumbnails = thumbnails
	)

}