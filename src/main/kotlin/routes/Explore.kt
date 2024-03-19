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
import parser.Explore
import parser.ResponseParser
import parser.parseExplore

suspend fun Innertube.explore(continuation: String? = null): Explore? {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		if (continuation == null) {
			setBody(
				Json.encodeToString(WebReqBodyWithBrowse("FEmusic_explore", this@explore.webContext))
			)
		} else {
			url {
				parameter("type", "next")
				parameter("ctoken", continuation)
				parameter("continuation", continuation)
			}
			setBody(
				Json.encodeToString(WebReqBody(this@explore.webContext))
			)
		}
	}.body()

	return ResponseParser.parseExplore(res)
}