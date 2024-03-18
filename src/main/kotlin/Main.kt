import client.Client
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import routes.album
import routes.artist
import java.io.File

suspend fun main() {
	val client = Client()
	val res = client.artist("UCDxKh1gFWeYsqePvgVzmPoQ")

	File("_test_.json").printWriter().use {
		it.println(Json.encodeToString(res))
	}
}