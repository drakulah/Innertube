package routes

import Innertube
import declare.Endpoint
import declare.WebReqBodyWithBrowse
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.MoodsAndGernes
import parser.ResponseParser
import parser.parseMoodsAndGernes

suspend fun Innertube.moodsAndGernes(): MoodsAndGernes {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		setBody(
			Json.encodeToString(WebReqBodyWithBrowse("FEmusic_moods_and_genres", this@moodsAndGernes.webContext))
		)
	}.body()

	return ResponseParser.parseMoodsAndGernes(res)
}