package parser

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import parser.partial.chunk.*
import parser.partial.preview.*
import utils.*

@Serializable
data class ExploreListTopic(
	val title: String,
	val subtitle: String?,
	val browseId: String?,
	val thumbnail: List<ThumbnailInfo>
)

@Serializable
data class ExploreListContainer(
	val topic: ExploreListTopic,
	val preContents: List<PreviewParser.ContentPreview>
)

@Serializable
data class Explore(
	val continuation: String?,
	val contents: List<ExploreListContainer>
)

fun ResponseParser.parseExplore(obj: JsonElement?): Explore? {

	var continuation: String? = null
	val contents = arrayListOf<ExploreListContainer>()

	(obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer")
		?: obj.path("continuationContents.sectionListContinuation"))?.let { sharedSection ->

		continuation = ChunkParser.parseContinuation(sharedSection.path("continuations[0]"))

		sharedSection.path("contents")
			?.jsonArray
			?.forEach { eachComp ->

				val preContents = arrayListOf<PreviewParser.ContentPreview>()

				(eachComp.path("musicCarouselShelfRenderer") ?: eachComp.path("musicShelfRenderer"))
					?.jsonObject
					?.let { sharedContainer ->

						var topicTitle: String
						var topicSubtitle: String?
						var topicBrowseId: String?
						var topicThumbnail: List<ThumbnailInfo>

						sharedContainer.path("header.musicCarouselShelfBasicHeaderRenderer").let { sharedHeader ->
							topicTitle = sharedHeader.path("title.runs[0].text").maybeStringVal?.nullifyIfEmpty() ?: return@forEach
							topicSubtitle = sharedHeader.path("strapline.runs[0].text").maybeStringVal?.nullifyIfEmpty()
							topicBrowseId = ChunkParser.parseId(sharedHeader.path("title.runs[0].navigationEndpoint"))
							topicThumbnail = ChunkParser.parseThumbnail(sharedHeader.path("thumbnail"))
						}

						sharedContainer.path("contents")
							?.jsonArray
							?.forEach { eachItem ->

								val itemRenderer =
									eachItem.path("musicTwoRowItemRenderer") ?: eachItem.path("musicResponsiveListItemRenderer")
								val itemType = ChunkParser.parseItemType(
									eachItem.path("musicTwoRowItemRenderer.navigationEndpoint")
										?: eachItem.path("musicResponsiveListItemRenderer.flexColumns[0].musicResponsiveListItemFlexColumnRenderer.text.runs[0].navigationEndpoint")
								)

								when (itemType) {
									ItemType.Video, ItemType.Song -> preContents.add(
										PreviewParser.parseTrackPreview(itemRenderer) ?: return@forEach
									)

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

						contents.add(
							ExploreListContainer(
								ExploreListTopic(
									title = topicTitle,
									subtitle = topicSubtitle,
									browseId = topicBrowseId,
									thumbnail = topicThumbnail
								),
								preContents
							)
						)
					}
			}
	}

	return Explore(
		continuation = continuation,
		contents = contents
	)
}