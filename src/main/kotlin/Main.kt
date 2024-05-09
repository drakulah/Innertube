import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import routes.*
import java.io.File

suspend fun main() {
	val innertube = Innertube()
//	val res = innertube.playlist(null, "4qmFsgL5ARIkVkxQTE5jSzRkSkp1MFZ0dHBMRGI4cndVbVZZOEJ5Zl96RUluGtABZW80QlVGUTZRMGRSYVVWRVl6Uk5SRmwzVGxWS1JFNXFiRVJPYTAxNVRsUkJiMEZWYW5jemNFZExiUzFIUlVFeFFVSlhhMVZwVVRKc1MxVldVa1pPVjNCVVpXeEtjbFV5ZEhkTlZURkhWMnBDYTFORlNrNVZhMlJLVGtkT2RWcEdXbWxXYkhCaFZEQldTMDVXY0hOUFZGcFRWbGQ0TVZKWFpETlRXR1JzWWxkMGVXUXhiRkphTUd4dFlUTm9RbFJUU1pJQkF3aTZCQSUzRCUzRA%3D%3D")
//	val res = innertube.artist("UCY1MGI_wki9WCH74C2bEzMg")
//	val res = innertube.search("Dildaara")
//	val res = innertube.search("Ve kamleya", params = "EgWKAQIQAWoQEAMQBBAJEAoQBRAVEBAQEQ%3D%3D")
	val res = innertube.home()

	File("_test_.json").printWriter().use {
		it.println(Json.encodeToString(res))
	}
}