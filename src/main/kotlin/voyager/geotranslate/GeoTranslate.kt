package voyager.geotranslate

import org.opensextant.geodesy.Geodetic2DPoint
import org.opensextant.geodesy.MGRS


class GeoTranslate {

  // Build the set of string patterns to try if Geodetic2DPoint function
  // does not support the format
  private val patterns = buildRegex()

  fun translate(s: String): String {
    // Try to parse the string as MGRS and Geodetic points.
    // If fails, try to match it using string patterns and reformat
    try {
      return repr(MGRS(s).toGeodetic2DPoint())
    } catch (e: Exception) {
      // do nothing. since we want to try more formats
    }

    try {
      return repr(Geodetic2DPoint(s))
    } catch (e: Exception) {
      // do nothing. since we want to try more formats
    }

    try {
      patterns.asIterable().forEach {
        if (it.matches(s))
          return translate(reformat(it, s))
      }
    } catch (e: Exception) {}

    return "Invalid Format: This format is not supported"
  }

  private fun repr(p: Geodetic2DPoint) : String {
    return "${p.latitudeAsDegrees}, ${p.longitudeAsDegrees}"
  }

  private fun buildRegex(): Sequence<Regex> {
    ClassLoader.getSystemResourceAsStream("patterns.txt").let {
      return it.bufferedReader().lineSequence().map {
        Regex(pattern = it)
      }
    }
  }

  private fun reformat(regex: Regex, s: String) : String {
    return s.let {
      val groups = regex.matchEntire(it)!!.groupValues

      // Switch [NSWE] indicators to suffixes since Geodesy does not support
      // them when used as prefixes
      groups.slice(1 until groups.size).joinToString {
        when (it[0]) {
          'N' -> "${it.substring(1)} N"
          'S' -> "${it.substring(1)} S"
          'W' -> "${it.substring(1)} W"
          'E' -> "${it.substring(1)} E"
          else -> it
        }
      }
    }.let {
      fixMinuteChar(it)
    }.let {
      fixDMSSpacing(it)
    }.let {
      changeOrder(it)
    }
  }

  private fun changeOrder(s: String) : String {
    if (!s.contains("[NSWE]".toRegex()))
      return s.split(",").reversed().joinToString()
    return s
  }

  private fun fixMinuteChar(s: String) : String {
    return s.replace("[´’]".toRegex(), "'")
  }

  fun fixDMSSpacing(s: String): String {
    return s.replace("([°´’'\"])".toRegex(), " ")
  }
}