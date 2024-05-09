package routes

import Innertube
import declare.Endpoint
import declare.WebReqBody
import declare.WebReqBodyWithBrowse
import declare.WebReqBodyWithVidListId
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.Playlist
import parser.ResponseParser
import parser.parsePlaylist

suspend fun Innertube.playlist(browseId: String? = null, continuation: String? = null): Playlist? {
	val res: JsonElement = this.webHttpClient.post(Endpoint.browse) {
		if (continuation == null && browseId != null) {
			setBody(
				Json.encodeToString(WebReqBodyWithBrowse(browseId, this@playlist.webContext))
			)
		} else if (continuation != null && browseId == null) {
			url {
				parameter("type", "next")
				parameter("ctoken", continuation)
				parameter("continuation", continuation)
			}
			setBody(
				Json.encodeToString(WebReqBody(this@playlist.webContext))
			)
		} else {
			return null
		}
	}.body()

	return ResponseParser.parsePlaylist(res)
}