The library supports the following formats

## Decimal Lat/Lon
 
``` 
dd.ddd -ddd.ddd
+dd.dddd -ddd.dddd
-dd.ddddd +ddd.ddddd
```

### Degrees/Minutes/Seconds/Direction
 
```
DD° MM' SS.SSSS' N DD° MM' SS.SSS' W
LM:DDMM N DDDMM W
LATM:DDMM N DDDMM W
LS:DDMMSS N DDDMMSS W
LATS:DDMMSS N DDDMMSS W
DDMMSS N DDDMMSS W
DDMMSS.S N DDDMMSS.S W
DD:MM:SS N DDD:MM:SS W
DD:MM:SS.S N DDD:MM:SS.S W
DDMMSS.SS N DDDMMSS.SS W
```
The LM and LATM prefixes indicate that minutes are the lowest level of granularity specified.
The LS and LATS prefixes indicate that seconds are the lowest level of granularity specified.

### UTM
```
UTM GGH nnnnnnE nnnnnnnN
GGH nnnnnnE nnnnnnnN
GGH nnnnnnnN nnnnnnE

 where
 GG = grid number (01-60)
 H = hemisphere (N or S)
 N = northing indicator
 E = easting indicator
 nnnnnn = the northing or easting value 
```

# Usage

```
import voyager.geotranslate.geotranslate

public class GeoTranslateTest {
    GeoTranslate translator = new GeoTranslate()
    translator.translate("40.76S 73.984W")
    // outputs: -40.75999999999999, -73.984
}

```
