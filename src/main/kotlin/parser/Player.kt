package parser

import json.*
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.*
import parser.partial.chunk.ChunkParser
import parser.partial.chunk.ThumbnailInfo
import parser.partial.chunk.parseThumbnail
import utils.nullifyIfEmpty

@Serializable
data class StreamInfo(
	val url: String,
	val mimeType: String,
	val bitrate: Int,
	val avgBitrate: Int,
	val lastModifiedTs: String,
	val contentLength: String,
	val approxDurationMs: String,
	val audioSampleRate: String?,
	val audioChannels: Int?,
	val loudnessDb: Float?
)

@Serializable
data class PlayerResponse(
	val status: String,
	val videoId: String,
	val title: String,
	val lengthSeconds: String?,
	val viewCount: String?,
	val isLiveContent: Boolean,
	val thumbnails: List<ThumbnailInfo>,
	val streams: List<StreamInfo>
)

fun ResponseParser.parsePlayer(obj: JsonElement?): PlayerResponse? {
	val streamsArr = arrayListOf<StreamInfo>()

	obj.path("streamingData.adaptiveFormats")?.jsonArray?.forEach {
		streamsArr.add(
			StreamInfo(
				url = it.jsonObject["url"].maybeStringVal?.nullifyIfEmpty() ?: return@forEach,
				mimeType = it.jsonObject["mimeType"].maybeStringVal?.nullifyIfEmpty() ?: return@forEach,
				bitrate = it.jsonObject["bitrate"].maybeIntVal ?: return@forEach,
				avgBitrate = it.jsonObject["averageBitrate"].maybeIntVal ?: return@forEach,
				lastModifiedTs = it.jsonObject["lastModified"].maybeStringVal?.nullifyIfEmpty() ?: return@forEach,
				contentLength = it.jsonObject["contentLength"].maybeStringVal?.nullifyIfEmpty() ?: return@forEach,
				approxDurationMs = it.jsonObject["approxDurationMs"].maybeStringVal?.nullifyIfEmpty() ?: return@forEach,
				audioSampleRate = it.jsonObject["audioSampleRate"].maybeStringVal?.nullifyIfEmpty(),
				audioChannels = it.jsonObject["audioChannels"].maybeIntVal,
				loudnessDb = it.jsonObject["loudnessDb"].maybeFloatVal
			)
		)
	}

	return PlayerResponse(
		status = obj.path("playabilityStatus.status").maybeStringVal?.nullifyIfEmpty() ?: return null,
		videoId = obj.path("videoDetails.videoId").maybeStringVal?.nullifyIfEmpty() ?: return null,
		title = obj.path("videoDetails.title").maybeStringVal?.nullifyIfEmpty() ?: return null,
		lengthSeconds = obj.path("videoDetails.lengthSeconds").maybeStringVal?.nullifyIfEmpty(),
		viewCount = obj.path("videoDetails.viewCount").maybeStringVal?.nullifyIfEmpty(),
		isLiveContent = obj.path("videoDetails.isLiveContent").maybeBoolVal ?: return null,
		thumbnails = ChunkParser.parseThumbnail(obj.path("videoDetails.thumbnail")),
		streams = streamsArr
	)
}