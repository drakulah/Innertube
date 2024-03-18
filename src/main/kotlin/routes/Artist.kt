package routes

import client.Client
import declare.Endpoint
import declare.WebReqBodyWithBrowse
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.Artist
import parser.ResponseParser
import parser.parseArtist

suspend fun Client.artist(browseId: String): Artist? {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		setBody(
			Json.encodeToString(WebReqBodyWithBrowse(browseId, this@artist.webContext))
		)
	}.body()

	return ResponseParser.parseArtist(res)
}