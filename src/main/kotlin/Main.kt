
import client.Client
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import routes.album
import routes.player
import java.io.File

suspend fun main() {
	val client = Client()
	val res = client.album("MPREb_gKbZIeGdtg4")

	File("home.json").printWriter().use {
		it.println(Json.encodeToString(res))
	}
}