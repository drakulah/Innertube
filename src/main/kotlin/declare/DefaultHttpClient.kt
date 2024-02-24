package declare

import io.ktor.client.*
import io.ktor.client.engine.cio.*
import io.ktor.client.plugins.*
import io.ktor.client.plugins.cache.*
import io.ktor.client.plugins.compression.*
import io.ktor.client.plugins.contentnegotiation.*
import io.ktor.client.plugins.cookies.*
import io.ktor.http.*
import io.ktor.serialization.kotlinx.json.*
import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.json.Json

@OptIn(ExperimentalSerializationApi::class)
fun defaultHttpClient(fn: (DefaultRequest.DefaultRequestBuilder) -> Unit): HttpClient {
	return HttpClient(CIO) {
		followRedirects = true
		expectSuccess = true

		BrowserUserAgent()

		install(HttpCache)
		install(HttpCookies)

		install(HttpRequestRetry) {
			retryOnException()
			constantDelay(3_000)
		}

		install(ContentNegotiation) {
			json(Json {
				ignoreUnknownKeys = true
				explicitNulls = false
				encodeDefaults = true
			})
		}

		install(ContentEncoding) {
			gzip(0.9f)
			deflate(0.8f)
		}

		defaultRequest {
			fn(this)
			contentType(ContentType.Application.Json)
		}
	}
}