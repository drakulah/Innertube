package parser.partial.chunk

import json.maybeIntVal
import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*

@Serializable
data class ThumbnailInfo(
	val url: String,
	val width: Int,
	val height: Int
)

/**
 * Provide __Object.background__ or __Object.thumbnail__ or __Object.thumbnailRenderer__
 */
fun ChunkParser.parseThumbnail(obj: JsonElement?): List<ThumbnailInfo> {
	val thumbnailArr = arrayListOf<ThumbnailInfo>()

	(
		obj?.path("thumbnails") ?:
		obj?.path("musicThumbnailRenderer.thumbnail.thumbnails") ?:
		obj?.path("croppedSquareThumbnailRenderer.thumbnail.thumbnails")
	)?.jsonArray?.forEach {
		thumbnailArr.add(
			ThumbnailInfo(
				url = it.path("url")?.maybeStringVal ?: return@forEach,
				width = it.path("width")?.maybeIntVal ?: return@forEach,
				height = it.path("height")?.maybeIntVal ?: return@forEach
			)
		)
	}

	return thumbnailArr
}