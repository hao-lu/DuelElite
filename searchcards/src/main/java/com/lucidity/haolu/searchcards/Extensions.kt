package com.lucidity.haolu.searchcards

import java.net.URLEncoder

// Move to common
fun String.encodeToHtmlAndReplacePlusWithUnderscore(): String {
    val html = URLEncoder.encode(this, "UTF-8")
    return html.replace("+", "_")
}