package parser

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import parser.partial.chunk.*
import parser.partial.preview.*
import utils.eatFiveStarDoNothing
import utils.mix
import utils.nullifyIfEmpty
import utils.removeEmpty

@Serializable
data class AlbumListTopic(
	val title: List<RunsText>,
	val subtitle: List<RunsText>,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class AlbumListContainer(
	val topic: AlbumListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
data class Album(
	val title: String,
	val desc: String?,
	val year: String?,
	val trackCount: String?,
	val albumType: AlbumType,
	val albumDuration: String?,
	val uploaders: List<Uploader>,
	val thumbnail: List<ThumbnailInfo>,
	val menu: List<Menu>,
	val track: List<TrackPreview>,
	val others: List<AlbumListContainer>
)

fun ResponseParser.parseAlbum(obj: JsonElement?): Album? {

	val track = arrayListOf<TrackPreview>()
	val others = arrayListOf<AlbumListContainer>()

	val title = (obj.path("title.runs[0].text")
		?: obj.path("flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].text")
			).maybeStringVal?.nullifyIfEmpty() ?: return null

	val subtitle = ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.subtitle.runs"))
		.mix(ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.secondTitle.runs")))
		.removeEmpty()

	val secondSubtitle = ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.secondSubtitle.runs"))
		.removeEmpty()

	val menu = ChunkParser.parseMenu(obj.path("header.musicDetailHeaderRenderer.menu"))
	val thumbnail = ChunkParser.parseThumbnail(obj.path("header.musicDetailHeaderRenderer.thumbnail"))
	val description = obj.path("header.musicDetailHeaderRenderer.description.runs").maybeStringVal?.nullifyIfEmpty()

	val fuck = obj?.jsonObject?.get("contents")
		?.jsonObject?.get("singleColumnBrowseResultsRenderer")
		?.jsonObject?.get("tabs")
		?.jsonArray?.get(0)
		?.jsonObject?.get("tabRenderer")
		?.jsonObject?.get("content")
		?.jsonObject?.get("sectionListRenderer")
		?.jsonObject?.get("contents")
		?.jsonArray?.get(0)
		?.jsonObject?.get("musicShelfRenderer")
		?.jsonObject?.get("contents")
		.toString()

	// println(fuck)

	obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.contents")
		?.jsonArray
		?.forEachIndexed { index, eachComp ->

			val preContents = arrayListOf<PreviewParser.ContentPreview>()

			(eachComp.path("musicCarouselShelfRenderer") ?: eachComp.path("musicShelfRenderer"))
				?.jsonObject
				?.let { sharedContainer ->
					val topic = AlbumListTopic(
						ChunkParser.parseRunsText(sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.title.runs")),
						ChunkParser.parseRunsText(sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.strapline.runs")),
						ChunkParser.parseThumbnail(sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer.thumbnail"))
					)

					var c = 0

					sharedContainer.path("contents")
						?.jsonArray
						?.forEach { eachItem ->

							println(++c)

							val itemRenderer =
								eachItem.path("musicTwoRowItemRenderer") ?: eachItem.path("musicResponsiveListItemRenderer")
							val itemType = ChunkParser.parseItemType(
								eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
									?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
							)

							println(itemType)

							when (index) {
								0 -> {
									when (itemType) {
										ItemType.Video, ItemType.Song -> track.add(
											PreviewParser.parseTrackPreview(itemRenderer) ?: return@forEach
										)

										else -> eatFiveStarDoNothing()
									}
								}

								else -> {
									when (itemType) {
										ItemType.AlbumPreview -> preContents.add(
											PreviewParser.parseAlbumPreview(itemRenderer) ?: return@forEach
										)

										ItemType.ArtistPreview -> preContents.add(
											PreviewParser.parseArtistPreview(itemRenderer) ?: return@forEach
										)

										ItemType.PlaylistPreview -> preContents.add(
											PreviewParser.parsePlaylistPreview(itemRenderer) ?: return@forEach
										)

										else -> eatFiveStarDoNothing()
									}
								}
							}

						}

					if (index != 0) others.add(AlbumListContainer(topic, preContents))
				}
		}

	if (title.isEmpty()) return null

	return Album(
		title,
		subtitle,
		secondSubtitle,
		description,
		thumbnail,
		menu,
		track,
		others
	)
}



