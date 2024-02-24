package routes

import client.Client
import declare.Endpoint
import declare.WebReqBody
import declare.WebReqBodyWithBrowse
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement

suspend fun Client.explore(continuation: String? = null) {
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
}