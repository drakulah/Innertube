package json

import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.jsonArray
import kotlinx.serialization.json.jsonObject
import utils.removeEmptyNTrim

fun JsonElement?.path(p: String): JsonElement? {
	var t: JsonElement? = this

	for (e in p.split('.').removeEmptyNTrim()) {
		if (e.matches(Regex("\\[\\d+\\]$"))) {
			var i = 0

			for (f in e.split('[').removeEmptyNTrim()) {
				val g = if (i != 0) {
					f.slice((f.length - 1) until f.length)
				} else {
					f
				}

				if (g.isEmpty()) continue

				t = if (g.matches(Regex("\\d+"))) {
					t?.jsonArray?.get(g.toInt())
				} else {
					t?.jsonObject?.get(g)
				}

				i++
			}
		} else {
			t = t?.jsonObject?.get(e)
		}
	}

	return t
}