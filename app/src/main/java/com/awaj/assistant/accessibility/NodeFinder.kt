package com.awaj.assistant.accessibility

import java.util.Locale

object NodeFinder {

    fun findElementByText(elements: List<UiElement>, queryText: String): UiElement? {
        val query = queryText.lowercase(Locale.getDefault()).trim()

        // 1. Exact match on text or contentDescription
        var match = elements.firstOrNull {
            it.text.lowercase(Locale.getDefault()) == query ||
            it.contentDescription.lowercase(Locale.getDefault()) == query
        }

        // 2. Contains match
        if (match == null) {
            match = elements.firstOrNull {
                it.text.lowercase(Locale.getDefault()).contains(query) ||
                it.contentDescription.lowercase(Locale.getDefault()).contains(query)
            }
        }

        // 3. Match by View ID
        if (match == null) {
            match = elements.firstOrNull {
                it.viewIdResourceName.lowercase(Locale.getDefault()).contains(query)
            }
        }

        return match
    }

    fun findClickableAncestorOrSelf(element: UiElement): UiElement {
        return element
    }
}
