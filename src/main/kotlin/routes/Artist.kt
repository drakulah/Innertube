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

suspend fun Client.artist(browseId: String) {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		setBody(
			Json.encodeToString(WebReqBodyWithBrowse(browseId, this@artist.webContext))
		)
	}.body()
}