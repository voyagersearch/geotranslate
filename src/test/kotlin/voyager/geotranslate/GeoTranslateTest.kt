package voyager.geotranslate

import org.junit.Test

internal class GeoTranslateTest {

  @Test
  fun translate() {
    ClassLoader.getSystemResourceAsStream("coordinates.txt").let {
      it.bufferedReader().lines().forEach {
        val translator = GeoTranslate()

        if (!it.startsWith('#') and it.isNotEmpty())
          println("$it : ${translator.translate(it)}")
      }
    }
  }

}