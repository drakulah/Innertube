package parser.partial.route

import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import parser.partial.chunk.*
import parser.partial.preview.*
import utils.eatFiveStarDoNothing
import utils.mix
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
data class AlbumRoute(
	val title: List<RunsText>,
	val subtitle: List<RunsText>,
	val secondSubtitle: List<RunsText>,
	val description: List<RunsText>,
	val thumbnail: List<ThumbnailInfo>,
	val menu: List<Menu>,
	val track: List<TrackPreview>,
	val others: List<AlbumListContainer>
)

fun RouteParser.parseAlbumRoute(obj: JsonElement?): AlbumRoute? {
	val track = arrayListOf<TrackPreview>()
	val others = arrayListOf<AlbumListContainer>()

	val title = ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.title.runs"))
		.removeEmpty()

	val subtitle = ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.subtitle.runs"))
		.mix(ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.secondTitle.runs")))
		.removeEmpty()

	val secondSubtitle = ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.secondSubtitle.runs"))
		.removeEmpty()

	val description = ChunkParser.parseRunsText(obj.path("header.musicDetailHeaderRenderer.description.runs"))
		.removeEmpty()

	val menu = ChunkParser.parseMenu(obj.path("menu"))

	val thumbnail = ChunkParser.parseThumbnail(obj.path("thumbnail"))

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

					sharedContainer.path("contents")
						?.jsonArray
						?.forEach { eachItem ->

							val itemRenderer =
								eachItem.path("musicTwoRowItemRenderer") ?: eachItem.path("musicResponsiveListItemRenderer")
							val itemType = ChunkParser.parseItemType(
								eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
									?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
							)

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

	if (title.isEmpty() || track.isEmpty()) return null

	return AlbumRoute(
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



