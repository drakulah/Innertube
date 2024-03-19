import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import routes.*
import java.io.File

suspend fun main() {
	val innertube = Innertube()
	val res = innertube.player("kPlSyYtE63M")

	File("_test_.json").printWriter().use {
		it.println(Json.encodeToString(res))
	}
}