package eu.kanade.tachiyomi.extension.all.foolslide

import android.util.Base64
import com.github.salomonbrys.kotson.get
import com.google.gson.JsonParser
import eu.kanade.tachiyomi.network.GET
import eu.kanade.tachiyomi.source.Source
import eu.kanade.tachiyomi.source.SourceFactory
import eu.kanade.tachiyomi.source.model.Page
import eu.kanade.tachiyomi.source.model.SManga
import okhttp3.Request
import org.jsoup.nodes.Document
import java.util.*

class FoolSlideFactory : SourceFactory {
    override fun createSources(): List<Source> = getAllFoolSlide()
}

fun getAllFoolSlide(): List<Source> {
    return listOf(
        JaminisBox(),
        ChampionScans(),
        HelveticaScans(),
        SenseScans(),
        SeaOtterScans(),
        KireiCake(),
        HiranoMoeScansBureau(),
        SilentSky(),
        Mangatellers(),
        IskultripScans(),
        PinkFatale(),
        AnataNoMotokare(),
        HatigarmScans(),
        DeathTollScans(),
        DKThias(),
        MangaichiScanlationDivision(),
        WorldThree(),
        TheCatScans(),
        AngelicScanlations(),
        DokiFansubs(),
        YuriIsm(),
        AjiaNoScantrad(),
        OneTimeScans(),
        TsubasaSociety(),
        Helheim(),
        MangaScouts(),
        StormInHeaven(),
        Lilyreader(),
        MidnightHaven(),
        Russification(),
        NieznaniReader(),
        EvilFlowers()
    )
}

class JaminisBox : FoolSlide("Jaimini's Box", "https://jaiminisbox.com", "en", "/reader") {

    override fun pageListParse(document: Document): List<Page> {
        val doc = document.toString()
        val base64Json = doc.substringAfter("JSON.parse(atob(\"").substringBefore("\"));")
        val decodeJson = String(Base64.decode(base64Json, Base64.DEFAULT))
        val json = JsonParser().parse(decodeJson).asJsonArray
        val pages = mutableListOf<Page>()
        json.forEach {
            pages.add(Page(pages.size, "", it["url"].asString))
        }
        return pages
    }

    override fun parseChapterDate(date: String): Long {
        try {
            val lcDate = date.toLowerCase()
            if (lcDate.endsWith("ago"))
                return parseRelativeDate(lcDate)

            //Handle 'yesterday' and 'today'
            var relativeDate: Calendar? = null
            if (lcDate.startsWith("yesterday")) {
                relativeDate = Calendar.getInstance()
                relativeDate.add(Calendar.DAY_OF_MONTH, -1) //yesterday
            } else if (lcDate.startsWith("today")) {
                relativeDate = Calendar.getInstance()
            }

            return relativeDate?.timeInMillis
                    ?: super.parseChapterDate(date)
        } catch(t: Throwable) {
            return 0L
        }
    }

    /**
     * Parses dates in this form:
     * `11 days ago`
     */
    private fun parseRelativeDate(date: String): Long {
        val trimmedDate = date.split(" ")

        if (trimmedDate[2] != "ago") return 0

        val number = trimmedDate[0].toIntOrNull() ?: return 0
        val unit = trimmedDate[1].removeSuffix("s") //Remove 's' suffix

        val now = Calendar.getInstance()

        //Map English unit to Java unit
        val javaUnit = when (unit) {
            "year", "yr" -> Calendar.YEAR
            "month" -> Calendar.MONTH
            "week" -> Calendar.WEEK_OF_MONTH
            "day" -> Calendar.DAY_OF_MONTH
            "hour", "hr" -> Calendar.HOUR
            "minute", "min" -> Calendar.MINUTE
            "second", "sec" -> Calendar.SECOND
            else -> return 0
        }

        now.add(javaUnit, -number)

        return now.timeInMillis
    }
}

class ChampionScans : FoolSlide("Champion Scans", "http://reader.championscans.com", "en")

class HelveticaScans : FoolSlide("Helvetica Scans", "http://helveticascans.com", "en", "/r")

class SenseScans : FoolSlide("Sense-Scans", "http://sensescans.com", "en", "/reader")

class SeaOtterScans : FoolSlide("Sea Otter Scans", "https://reader.seaotterscans.com", "en")

class KireiCake : FoolSlide("Kirei Cake", "https://reader.kireicake.com", "en")

class HiranoMoeScansBureau : FoolSlide("HiranoMoe Scans Bureau", "http://hiranomoe.com", "en", "/r")

class SilentSky : FoolSlide("Silent Sky", "http://reader.silentsky-scans.net", "en")

class Mangatellers : FoolSlide("Mangatellers", "http://www.mangatellers.gr", "en", "/reader/reader") {
    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl$urlModifier/list/$page/", headers)
    }
}

class IskultripScans : FoolSlide("Iskultrip Scans", "http://www.maryfaye.net", "en", "/reader")

class PinkFatale : FoolSlide("PinkFatale", "http://manga.pinkfatale.net", "en")

class AnataNoMotokare : FoolSlide("Anata no Motokare", "https://motokare.maos.ca", "en")

// Has other languages too but it is difficult to differentiate between them
class HatigarmScans : FoolSlide("Hatigarm Scans", "http://hatigarmscans.eu", "en", "/hs") {
    override fun chapterListSelector() = "div.list-group div.list-group-item:not(.active)"

    override val chapterDateSelector = "div.label"

    override val chapterUrlSelector = ".title > a"

    override fun popularMangaSelector() = ".well > a"

    override fun latestUpdatesSelector() = "div.latest > div.row"

    override val mangaDetailsInfoSelector = "div.col-md-9"

    override val mangaDetailsThumbnailSelector = "div.thumb > img"
}

class DeathTollScans : FoolSlide("Death Toll Scans", "https://reader.deathtollscans.net", "en")

class DKThias : FoolSlide("DKThias Scanlations", "http://reader.dkthias.com", "en", "/reader") {
    override fun popularMangaRequest(page: Int): Request {
        return GET("$baseUrl$urlModifier/list/$page/", headers)
    }
}

class MangaichiScanlationDivision : FoolSlide("Mangaichi Scanlation Division", "http://mangaichiscans.mokkori.fr", "en", "/fs")

class WorldThree : FoolSlide("World Three", "http://www.slide.world-three.org", "en")

class TheCatScans : FoolSlide("The Cat Scans", "https://reader.thecatscans.com", "en")

class AngelicScanlations : FoolSlide("Angelic Scanlations", "http://www.angelicscans.net", "en", "/foolslide") {
    override fun latestUpdatesSelector() = "div.list > div.releases"

    override fun popularMangaSelector() = ".grouped > .series-block"

    override fun mangaDetailsParse(document: Document): SManga {
        return SManga.create().apply {
            thumbnail_url = document.select(".preview > img").attr("src")

            val info = document.select(".data").first()
            title = info.select("h2.title").text().trim()
            val authorArtist = info.select(".author").text().split("/")
            author = authorArtist.getOrNull(0)?.trim()
            artist = authorArtist.getOrNull(1)?.trim()

            description = info.ownText().trim()
        }
    }

    override fun chapterListSelector() = ".list > .release"

    override val chapterDateSelector = ".metadata"
}

class DokiFansubs : FoolSlide("Doki Fansubs", "https://kobato.hologfx.com", "en", "/reader")

class YuriIsm : FoolSlide("Yuri-ism", "http://reader.yuri-ism.com", "en", "/slide")

class AjiaNoScantrad : FoolSlide("Ajia no Scantrad", "http://ajianoscantrad.fr", "fr", "/reader")

class OneTimeScans : FoolSlide("One Time Scans", "https://otscans.com", "en", "/foolslide")

class TsubasaSociety : FoolSlide("Tsubasa Society", "https://www.tsubasasociety.com", "en", "/reader/master/Xreader")

class Helheim : FoolSlide("Helheim", "http://helheim.pl", "pl", "/reader")

class MangaScouts : FoolSlide("MangaScouts", "http://onlinereader.mangascouts.org", "de")

class StormInHeaven : FoolSlide("Storm in Heaven", "http://www.storm-in-heaven.net", "it", "/reader-sih")

class Lilyreader : FoolSlide("Lilyreader", "https://manga.smuglo.li", "en")

class MidnightHaven : FoolSlide("Midnight Haven", "http://midnighthaven.shounen-ai.net", "en", "/reader")

class Russification : FoolSlide("Русификация", "http://rusmanga.ru", "ru")

class NieznaniReader : FoolSlide("Nieznani", "http://reader.nieznani.mynindo.pl", "pl")

class EvilFlowers : FoolSlide("Evil Flowers", "http://reader.evilflowers.com", "en")
