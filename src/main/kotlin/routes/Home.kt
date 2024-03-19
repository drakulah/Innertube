package routes

import Innertube
import declare.Endpoint
import declare.WebReqBody
import declare.WebReqBodyWithBrowse
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.Home
import parser.ResponseParser
import parser.parseHome

suspend fun Innertube.home(continuation: String? = null): Home? {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		if (continuation == null) {
			setBody(
				Json.encodeToString(WebReqBodyWithBrowse("FEmusic_home", this@home.webContext))
			)
		} else {
			url {
				parameter("type", "next")
				parameter("ctoken", continuation)
				parameter("continuation", continuation)
			}
			setBody(
				Json.encodeToString(WebReqBody(this@home.webContext))
			)
		}
	}.body()

	return ResponseParser.parseHome(res)
}