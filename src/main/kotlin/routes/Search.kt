package routes

import Innertube
import declare.*
import io.ktor.client.call.*
import io.ktor.client.request.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonElement
import parser.*

suspend fun Innertube.search(query: String? = null, continuation: String? = null, params: String? = null): Search? {
	val res: JsonElement = this.webHttpClient.post(Endpoint.search) {
		if (continuation == null && query != null) {
			if (params == null)
				setBody(
					Json.encodeToString(WebReqBodyWithQuery(query, this@search.webContext))
				)
			else
				setBody(
					Json.encodeToString(WebReqBodyWithQueryAndParams(query, params, this@search.webContext))
				)
		} else if (continuation != null && query == null) {
			url {
				parameter("type", "next")
				parameter("ctoken", continuation)
				parameter("continuation", continuation)
			}
			setBody(
				Json.encodeToString(WebReqBody(this@search.webContext))
			)
		} else {
			return null
		}
	}.body()

	return ResponseParser.parseSearch(res)
}