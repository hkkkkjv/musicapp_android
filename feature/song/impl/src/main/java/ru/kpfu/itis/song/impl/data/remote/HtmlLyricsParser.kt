package ru.kpfu.itis.song.impl.data.remote
import android.util.Log
import org.jsoup.Jsoup
import javax.inject.Inject

class HtmlLyricsParser @Inject constructor() {

    fun parseLyricsFromHtml(html: String): String? {
        return try {
            val doc = Jsoup.parse(html)
            doc.outputSettings().prettyPrint(false)
            doc.select("br").append("\\n")
            val lyricsContainers = doc.select("div[data-lyrics-container='true']")

            if (lyricsContainers.isEmpty()) {
                Log.w("LyricsParser", "⚠️ Контейнер лирики не найден")
                return null
            }

            val lyrics = lyricsContainers.mapNotNull { container ->
                container
                    .text()
                    .replace("\\n", "\n")
                    .trim()
                    .takeIf { it.isNotEmpty() }
            }.joinToString("\n\n")

            Log.i("LyricsParser", "✅ Лирика распарсена: ${lyrics.length} символов")
            lyrics.takeIf { it.isNotEmpty() }
        } catch (e: Exception) {
            Log.e("LyricsParser", "❌ Ошибка парсинга: ${e.message}")
            null
        }
    }
}
