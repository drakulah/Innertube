package routes

import Innertube
import declare.Endpoint
import declare.WebReqBodyWithVidListId
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Innertube.upNext(videoId: String, playlistId: String) {
	val res: JsonElement = this.webHttpClient.post(Endpoint.next) {
		setBody(
			Json.encodeToString(WebReqBodyWithVidListId(videoId, playlistId, this@upNext.webContext))
		)
	}.body()
}