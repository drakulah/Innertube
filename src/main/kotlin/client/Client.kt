package client

import declare.*

class Client(
	visitorData: String = defaultVisitorData
) {

	val webHttpClient = defaultHttpClient {
		it.url(scheme = "https", host = "music.youtube.com") {
			parameters.append("prettyPrint", "false")
			parameters.append("key", ApiKey.Web)
		}
	}

	val androidHttpClient = defaultHttpClient {
		it.url(scheme = "https", host = "music.youtube.com") {
			parameters.append("prettyPrint", "false")
			parameters.append("key", ApiKey.Android)
		}
	}

	val webContext = WebReqContext(
		client = WebReqClient("US", "en", "WEB_REMIX", "1.20230104.01.00", visitorData)
	)

	val androidContext = AndroidReqContext(
		client = AndroidReqClient("US", "en", "ANDROID_MUSIC", "5.39.52", 32, visitorData)
	)
}