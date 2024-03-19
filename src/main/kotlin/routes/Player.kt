package routes

import Innertube
import declare.AndroidReqBodyPlayer
import declare.Endpoint
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.PlayerResponse
import parser.ResponseParser
import parser.parsePlayer

suspend fun Innertube.player(videoId: String): PlayerResponse? {
	val res: JsonElement = this.androidHttpClient.post(Endpoint.player) {
		setBody(
			Json.encodeToString(AndroidReqBodyPlayer(videoId, this@player.androidContext))
		)
	}.body()

	return ResponseParser.parsePlayer(res)
}