package voyager.geotranslate

import org.opensextant.geodesy.Geodetic2DPoint
import org.opensextant.geodesy.MGRS
import kotlin.math.floor


class GeoTranslate {

  // Build the set of string patterns to try if Geodetic2DPoint function
  // does not support the format
  private val patterns = buildRegex()
  private var lat: String = ""
  private var lon: String = ""

  fun translate(p: String): String {
    var s = p
    // Try to parse the string as MGRS and Geodetic points.
    // If fails, try to match it using string patterns and reformat
    try {
      return repr(MGRS(s).toGeodetic2DPoint())
    } catch (e: Exception) {
      // do nothing. since we want to try more formats
    }

    try {
      return repr(UTM(s).toGeodetic2DPoint())
    } catch (e: Exception) {
      // do nothing. since we want to try more formats
    }

    try {
      return repr(Geodetic2DPoint(s))
    } catch (e: Exception) {
      patterns.asIterable().forEach {
        if (it.matches(s)){
          reformat(it, s)
          return translate("$lon, $lat")
        }
      }
    }

    return "Invalid Format: This format is not supported"
  }

  private fun repr(p: Geodetic2DPoint) : String {
    return "${p.latitudeAsDegrees}, ${p.longitudeAsDegrees}"
  }

  private fun buildRegex(): Sequence<Regex> {
    ClassLoader.getSystemResourceAsStream("patterns.txt").let {
      return it.bufferedReader().lineSequence().mapNotNull {
        Regex(pattern = it)
      }
    }
  }

  private fun reformat(regex: Regex, s: String) {
    s.let {
      when {
        it.startsWith("LM") -> parseDMSString(regex, it)
        it.startsWith("LATM") -> parseDMSString(regex, it)
        it.startsWith("LS") -> parseDMSString(regex, it)
        it.startsWith("LATS") -> parseDMSString(regex, it)
        // find a better way for this
        it.matches("([\\d.]{5,}[NS])[ ]*([\\d.]{5,}[WE])".toRegex()) -> parseDMSString(regex, it)
        it.matches("[NSWE].*".toRegex()) -> fixPrefixes(regex, it)
        else -> {
          val groups = regex.matchEntire(it)!!.groupValues
          lat = groups[1]
          lon = groups[2]
        }
      }
    }
    lat = fixDMSSpacing(lat)
    lon = fixDMSSpacing(lon)
  }

  private fun parseDMSString(regex: Regex, s: String) {
    var groups = regex.matchEntire(s)!!.groupValues

    groups = groups.slice(1 until groups.size).map {
      val direction = it.takeLast(1)
      var coord = it.substringBefore(direction).replace("([:])".toRegex(), "")
      val segments = mutableListOf<Any>()
      while (coord != "0.0") {
        if (segments.isEmpty())
          segments.add(0, coord.toDouble()%100)
        else
          segments.add(0, coord.toDouble().toInt()%100)
        coord = floor(coord.toDouble()/100).toString()
      }
      segments.add(direction)
      segments.joinToString(" ")
    }
    lat = groups[0]
    lon = groups[1]
  }

  private fun fixPrefixes(regex: Regex, s: String) {
    var groups = regex.matchEntire(s)!!.groupValues

    // Switch [NSWE] indicators to suffixes since Geodesy does not support
    // them when used as prefixes
    groups = groups.slice(1 until groups.size).map {
      when (it[0]) {
        'N' -> "${it.substring(1)} N"
        'S' -> "${it.substring(1)} S"
        'W' -> "${it.substring(1)} W"
        'E' -> "${it.substring(1)} E"
        else -> it
      }
    }
    lat = groups[0]
    lon = groups[1]
  }

  private fun fixDMSSpacing(s: String): String {
    return s.replace("°", "° ")
        .replace("([´’'])".toRegex(), "' ")
        .replace("([\"])".toRegex(), "\" ")
  }
}