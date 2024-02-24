
import client.Client
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import routes.player
import java.io.File

suspend fun main() {
	val client = Client()
	val res = client.player("qnQCd_nZn_g")

	File("home.json").printWriter().use {
		it.println(Json.encodeToString(res))
	}
}