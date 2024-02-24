package routes

import client.Client
import declare.AndroidReqBodyPlayer
import declare.Endpoint
import io.ktor.client.call.*
import io.ktor.client.request.*
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import parser.partial.chunk.ChunkParser
import parser.partial.chunk.ThumbnailInfo
import parser.partial.chunk.parseThumbnail

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
	val lengthSeconds: String,
	val viewCount: String,
	val isLiveContent: Boolean,
	val thumbnails: List<ThumbnailInfo>,
	val streams: List<StreamInfo>
)

suspend fun Client.player(videoId: String): PlayerResponse {
	val res: JsonElement = this.androidHttpClient.post(Endpoint.player) {
		setBody(
			Json.encodeToString(AndroidReqBodyPlayer(videoId, this@player.androidContext))
		)
	}.body()

	val streamsArr = arrayListOf<StreamInfo>()
	val thumbnailArr = ChunkParser.parseThumbnail(res.path("videoDetails.thumbnail"))

	res.path("streamingData.adaptiveFormats")?.jsonArray?.forEach {
		streamsArr.add(
			StreamInfo(
				url = it.jsonObject["url"]?.jsonPrimitive?.content ?: return@forEach,
				mimeType = it.jsonObject["mimeType"]?.jsonPrimitive?.content ?: return@forEach,
				bitrate = it.jsonObject["bitrate"]?.jsonPrimitive?.int ?: return@forEach,
				avgBitrate = it.jsonObject["averageBitrate"]?.jsonPrimitive?.int ?: return@forEach,
				lastModifiedTs = it.jsonObject["lastModified"]?.jsonPrimitive?.content ?: return@forEach,
				contentLength = it.jsonObject["contentLength"]?.jsonPrimitive?.content ?: return@forEach,
				approxDurationMs = it.jsonObject["approxDurationMs"]?.jsonPrimitive?.content ?: return@forEach,
				audioSampleRate = it.jsonObject["audioSampleRate"]?.jsonPrimitive?.content,
				audioChannels = it.jsonObject["audioChannels"]?.jsonPrimitive?.intOrNull,
				loudnessDb = it.jsonObject["loudnessDb"]?.jsonPrimitive?.floatOrNull
			)
		)
	}

	return PlayerResponse(
		status = res.path("playabilityStatus.status")?.jsonPrimitive?.content
			?: throw Exception("Invalid response"),
		videoId = res.path("videoDetails.videoId")?.jsonPrimitive?.content
			?: throw Exception("Invalid response"),
		title = res.path("videoDetails.title")?.jsonPrimitive?.content
			?: throw Exception("Invalid response"),
		lengthSeconds = res.path("videoDetails.lengthSeconds")?.jsonPrimitive?.content
			?: throw Exception("Invalid response"),
		viewCount = res.path("videoDetails.viewCount")?.jsonPrimitive?.content ?: throw Exception(
			"Invalid response"
		),
		isLiveContent = res.path("videoDetails.isLiveContent")?.jsonPrimitive?.boolean
			?: throw Exception("Invalid response"),
		thumbnails = thumbnailArr,
		streams = streamsArr
	)
}