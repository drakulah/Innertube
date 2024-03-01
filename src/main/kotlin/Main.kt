import client.Client
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import routes.album
import java.io.File

suspend fun main() {
	val client = Client()
	val res = client.album("MPREb_flhIQLh6cWj")

	File("_test_.json").printWriter().use {
		it.println(Json.encodeToString(res))
	}
}