package parser.partial.preview

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import parser.partial.chunk.*
import utils.*

@Serializable
data class TrackPreview(
	val id: String,
	val title: String,
	val durationText: String?,
	val trackPlays: String?,
	val album: AlbumBasicInfo?,
	val menu: List<Menu>,
	val uploaders: List<Uploader>,
	val thumbnails: List<ThumbnailInfo>
) : PreviewParser.ContentPreview()

fun PreviewParser.parseTrackPreview(obj: JsonElement?): TrackPreview? {

	var id: String? = null
	var title: String? = null
	var trackPlays: String? = null
	var durationText: String? = null
	var album: AlbumBasicInfo? = null

	val uploaders = arrayListOf<Uploader>()

	/************************************************/

	obj?.let { raw ->
		id = ChunkParser.parseId(raw.path("navigationEndpoint"))
		title = obj.path("title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
		durationText = obj.path("lengthText.runs[0].text").maybeStringVal?.nullifyIfEmpty()

		mixedJsonArray(
			raw.path("subtitle.runs"),
			raw.path("lengthText.runs"),
			raw.path("secondTitle.runs"),
			raw.path("longBylineText.runs"),
			raw.path("shortBylineText.runs"),
		).forEach {

			val tempText = it.path("text").maybeStringVal.nullifyIfEmpty() ?: return@forEach
			val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))

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

				else -> when {
					tempText.isDurationText() -> durationText = tempText
					tempText.isTrackPlays() -> trackPlays = tempText
					tempText.isMaybeTitle() -> uploaders.add(
						Uploader(
							title = tempText,
							isArtist = false,
							browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
						)
					)

					else -> eatFiveStarDoNothing()
				}
			}
		}
	}

	/************************************************/

	mixedJsonArray(
		obj.path("flexColumns"),
		obj.path("fixedColumns")
	).forEach { innerRaw ->

		(innerRaw.path("musicResponsiveListItemFlexColumnRenderer.text.runs")
			?: innerRaw.path("musicResponsiveListItemFixedColumnRenderer.text.runs"))
			?.jsonArray
			?.forEach {
				val tempText = it.path("text").maybeStringVal.nullifyIfEmpty() ?: return@forEach
				val tempType = ChunkParser.parseItemType(it.path("navigationEndpoint"))

				when (tempType) {
					ItemType.Song, ItemType.Video -> {
						title = tempText
						ChunkParser.parseId(it.path("navigationEndpoint"))?.let { e -> id = e }
					}

					ItemType.ArtistPreview, ItemType.UserChannelPreview -> {
						uploaders.add(
							Uploader(
								title = tempText,
								isArtist = tempType == ItemType.ArtistPreview,
								browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
							)
						)
					}

					ItemType.AlbumPreview -> {
						album = AlbumBasicInfo(
							title = tempText,
							browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
						)
					}

					else -> when {
						tempText.isDurationText() -> durationText = tempText
						tempText.isTrackPlays() -> trackPlays = tempText
						tempText.isMaybeTitle() -> uploaders.add(
							Uploader(
								title = tempText,
								isArtist = false,
								browseId = ChunkParser.parseId(it.path("navigationEndpoint"))
							)
						)

						else -> eatFiveStarDoNothing()
					}
				}
			}

	}

	/************************************************/

	return TrackPreview(
		id = id ?: return null,
		title = title ?: return null,
		trackPlays = trackPlays,
		durationText = durationText,
		album = album,
		uploaders = uploaders,
		thumbnails = ChunkParser.parseThumbnail(obj.path("thumbnail") ?: obj.path("thumbnailRenderer")),
		menu = ChunkParser.parseMenu(obj.path("menu") ?: obj.path("musicTwoRowItemRenderer.menu")),
	)
}