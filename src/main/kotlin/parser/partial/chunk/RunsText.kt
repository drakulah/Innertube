package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
data class RunsText(
	val text: String
)

fun ChunkParser.parseRunsText(obj: JsonElement?): List<RunsText> {
	val texts = arrayListOf<RunsText>()

	obj?.jsonArray?.forEach {
		val t = it.path("text")?.maybeStringVal

		if (t.isNullOrEmpty()) return@forEach

		texts.add(RunsText(text = t))
	}

	return texts
}