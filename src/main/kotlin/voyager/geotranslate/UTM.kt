package voyager.geotranslate

import com.sun.media.sound.InvalidFormatException
import org.opensextant.geodesy.Geodetic2DPoint
import org.opensextant.geodesy.UTM

class UTM constructor(s: String) {

  private var zone: Int
  private var hemisphere : Char
  private var easting : Double
  private var northing : Double

  /**
   * This class supports the following UTM format
   *
   * GGH nnnnnnE nnnnnnnN
   * GGH nnnnnnnN nnnnnnE
   *
   * where
   * GG = grid number (01-60)
   * H = hemisphere (N or S)
   * N = northing indicator
   * E = easting indicator
   * nnnnnn = the northing or easting value**/

  init {
    var coords = s
    if (coords.contains("UTM"))
      coords = coords.substringAfter("UTM")

    var segments = coords.split(" ".toRegex())

    if (segments.size == 3) {
      zone = segments[0].substring(0..1).toInt()
      hemisphere = segments[0].substring(2).single()
      if (segments[1].contains("E")) {
        easting = segments[1].substringBefore("E").toDouble()
        northing = segments[2].substringBefore("N").toDouble()
      } else {
        easting = segments[2].substringBefore("E").toDouble()
        northing = segments[1].substringBefore("N").toDouble()
      }
    } else if (segments.size >= 5) {
      zone = segments[0].toInt()
      hemisphere = segments[1].single()
      if (segments[3].equals("E", ignoreCase = true)) {
        easting = segments[2].replace("[A-Za-z]".toRegex(), "").toDouble()
        northing = segments[4].replace("[A-Za-z]".toRegex(), "").toDouble()
      } else {
        easting = segments[4].replace("[A-Za-z]".toRegex(), "").toDouble()
        northing = segments[2].replace("[A-Za-z]".toRegex(), "").toDouble()
      }
    } else {
      throw InvalidFormatException("This format is not supported. Supported formats include: GGH nnnnnn[EN] nnnnnnn[EN] or GG H nnnnnn [EN] nnnnnnn [EN]")
    }
  }

  fun toGeodetic2DPoint() : Geodetic2DPoint {
    return UTM(zone, hemisphere, easting, northing).geodetic
  }
}