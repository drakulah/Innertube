package routes

import client.Client
import declare.Endpoint
import declare.WebReqBodyWithBrowse
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.Album
import parser.ResponseParser
import parser.parseAlbum

suspend fun Client.album(browseId: String): Album? {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		setBody(
			Json.encodeToString(WebReqBodyWithBrowse(browseId, this@album.webContext))
		)
	}.body()

	return ResponseParser.parseAlbum(res)
}