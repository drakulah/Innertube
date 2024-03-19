package parser

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import parser.partial.preview.MoodPreview
import parser.partial.preview.PreviewParser
import parser.partial.preview.parseMoodPreview
import utils.nullifyIfEmpty

@Serializable
data class MoodsAndGernesListContainer(
	val topic: String,
	val preContents: List<MoodPreview>
)

@Serializable
data class MoodsAndGernes(
	val contents: List<MoodsAndGernesListContainer>
)

fun ResponseParser.parseMoodsAndGernes(obj: JsonElement?): MoodsAndGernes {

	val contents = arrayListOf<MoodsAndGernesListContainer>()

	obj.path("contents.singleColumnBrowseResultsRenderer.tabs[0].tabRenderer.content.sectionListRenderer.contents")
		?.jsonArray
		?.forEach { eachComp ->

			eachComp.path("gridRenderer")?.let { sharedRenderer ->
				val topicText =
					sharedRenderer.path("header.gridHeaderRenderer.title.runs[0].text").maybeStringVal?.nullifyIfEmpty()
						?: return@forEach
				val preContents = arrayListOf<MoodPreview>()

				sharedRenderer.path("items")
					?.jsonArray
					?.forEach { eachItem ->
						preContents.add(
							PreviewParser.parseMoodPreview(eachItem.path("musicNavigationButtonRenderer")) ?: return@forEach
						)
					}

				if (preContents.isNotEmpty()) contents.add(
					MoodsAndGernesListContainer(
						topic = topicText,
						preContents = preContents
					)
				)
			}
		}

	return MoodsAndGernes(
		contents = contents
	)
}