package parser.partial.preview

import json.maybeLongVal
import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import parser.partial.chunk.ChunkParser
import parser.partial.chunk.RunsText
import parser.partial.chunk.parseRunsText
import utils.removeEmpty

@Serializable
data class MoodPreview(
	val title: List<RunsText>,
	val color: Long,
	val browseId: String,
	val params: String
): PreviewParser.ContentPreview()

fun PreviewParser.parseMoodPreview(obj: JsonElement?): MoodPreview? {

	val color = obj.path("solid.leftStripeColor").maybeLongVal

	val params = obj.path("clickCommand.browseEndpoint.params").maybeStringVal

	val browseId = obj.path("clickCommand.browseEndpoint.browseId").maybeStringVal

	val title = ChunkParser.parseRunsText(obj.path("buttonText.runs")).removeEmpty()

	if (title.isEmpty()
		|| browseId.isNullOrEmpty()
		|| params.isNullOrEmpty()
		|| color == null
	) return null

	return MoodPreview(
		title,
		color,
		params,
		browseId,
	)
}