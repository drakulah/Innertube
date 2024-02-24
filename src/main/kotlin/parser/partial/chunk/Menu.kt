package parser.partial.chunk

import json.maybeStringVal
import json.path
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray

@Serializable
sealed class Menu

@Serializable
data class Radio(
	val playlistId: String
) : Menu()

@Serializable
data class Album(
	val browseId: String
) : Menu()

@Serializable
data class Artist(
	val browseId: String
) : Menu()

@Serializable
data class Credit(
	val browseId: String
) : Menu()

fun ChunkParser.parseMenu(obj: JsonElement?): List<Menu> {
	val menu = arrayListOf<Menu>()

	obj?.path("menuRenderer.items")?.jsonArray?.forEach {
		it.path("menuNavigationItemRenderer")?.let { navItem ->

			when (navItem.path("text.runs[0].text")?.maybeStringVal?.trim()?.lowercase()) {
				"start radio" -> menu.add(
					Radio(
						playlistId = navItem.path("navigationEndpoint.watchEndpoint.playlistId")?.maybeStringVal
							?: navItem.path("navigationEndpoint.watchPlaylistEndpoint.playlistId")?.maybeStringVal ?: return@forEach,
					)
				)

				"go to album" -> menu.add(
					Album(
						browseId = navItem.path("navigationEndpoint.browseEndpoint.browseId")?.maybeStringVal ?: return@forEach,
					)
				)

				"go to artist" -> menu.add(
					Artist(
						browseId = navItem.path("navigationEndpoint.browseEndpoint.browseId")?.maybeStringVal ?: return@forEach,
					)
				)

				"view song credits" -> menu.add(
					Credit(
						browseId = navItem.path("navigationEndpoint.browseEndpoint.browseId")?.maybeStringVal ?: return@forEach,
					)
				)
			}
		}
	}

	return menu
}