package utils

import parser.partial.chunk.RunsText

fun List<RunsText>.removeEmpty(): List<RunsText> {
	val arr = arrayListOf<RunsText>()

	for (e in this) if (e.text.trim().isNotEmpty()) arr.add(e)

	return arr
}

fun List<RunsText>.removeEmptyNTrim(): List<RunsText> {
	val arr = arrayListOf<RunsText>()

	for (e in this) if (e.text.trim().isNotEmpty()) arr.add(e.copy(text = e.text.trim()))

	return arr
}

fun List<RunsText>.mix(strArr: List<RunsText>): List<RunsText> {
	(this as ArrayList<RunsText>).addAll(strArr)
	return this
}