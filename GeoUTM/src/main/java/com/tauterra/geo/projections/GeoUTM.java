/**
 * Copyright 2017 tauTerra, LLC, Nicholas Folse.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 * Adapted from : http://www.rcn.montana.edu/resources/converter.aspx
 * Ref: http://www.uwgb.edu/dutchs/UsefulData/UTMFormulas.HTM
 *
 * A UTM -> Lat/Long (or vice versa) converter adapted from the script used at
 *     http://www.uwgb.edu/dutchs/UsefulData/ConvertUTMNoOZ.HTM
 *
 */
package com.tauterra.geo.projections;

import com.tauterra.geo.Coordinate;
import com.tauterra.geo.LatLonCoord;
import java.util.function.Function;

/**
 *
 * @author Nicholas Folse
 */
public class GeoUTM {

    /**
     * Get the UTM zone for a specified longitude and latitude
     *
     * @param latLon latitude-longitude coordinate
     * @return UTM zone corresponding to specified latLon
     */
    public static Integer findUTMZone(LatLonCoord latLon) {
        double lngd = latLon.getLon();
        int utmz = 1 + new Double(Math.floor((lngd + 180.0) / 6.0)).intValue();
        return utmz;
    }

    public static Function<LatLonCoord, Coordinate> getInstance(final Integer zone, final Datum datum) {
        final Integer utmz = zone;
        final Integer zcm = 3 + 6 * (utmz - 1) - 180;
        final Datum d = datum;

        // constants taken from or calculated from the datum
        final double a = d.eqRad;
        final double f = 1.0 / d.flat;
        final double b = a * (1 - f);   // polar radius
        final double esq = (1 - Math.pow((b / a), 2.0));    // e-squared for use in expansions
        final double e = Math.sqrt(esq);                  // eccentricity
        final double e0 = e / Math.sqrt(1 - e);           // Called e prime in reference
        final double e0sq = e * e / (1.0 - Math.pow(e, 2.0));     // squared - always even powers

        // constants used in calculations
        final double k = 1.0;
        final double k0 = 0.9996;
        final double drad = Math.PI / 180.0;

        return (LatLonCoord latlon) -> {
            final double lngd = latlon.getLon();
            final double lat = latlon.getLat();
            final double phi = lat * drad;
            double M;

            final double N = a / Math.sqrt(1 - Math.pow(e * Math.sin(phi), 2.0));
            final double T = Math.pow(Math.tan(phi), 2.0);
            final double C = e0sq * Math.pow(Math.cos(phi), 2.0);
            final double A = (lngd - zcm) * drad * Math.cos(phi);

            // calculate M (USGS style)
            M = phi * (1.0 - esq * (0.25 + esq * (3.0 / 64.0 + 5.0 * esq / 256.0)));
            M -= Math.sin(2.0 * phi) * (esq * (3.0 / 8.0 + esq * (3.0 / 32.0 + 45.0 * esq / 1024.0)));
            M += Math.sin(4.0 * phi) * (esq * esq * (15.0 / 256.0 + esq * 45.0 / 1024.0));
            M -= Math.sin(6.0 * phi) * (esq * esq * esq * (35.0 / 3072.0));
            M *= a;     //Arc length along standard meridian

            final double M0 = 0.0;        // if another point of origin is used than the equator

            // calculate the UTM values...
            // first the easting
            double x = k0 * N * A * (1 + A * A * ((1 - T + C) / 6.0 + A * A * (5 - 18 * T + T * T + 72 * C - 58 * e0sq) / 120.0)); //Easting relative to CM
            x += 500000; // standard easting

            // now the northing
            double y = k0 * (M - M0 + N * Math.tan(phi) * (A * A * (0.5 + A * A * ((5 - T + 9 * C + 4 * C * C) / 24.0 + A * A * (61 - 58 * T + T * T + 600 * C - 330 * e0sq) / 720.0))));    // first from the equator
            if (y < 0) {
                y = 10000000 + y;   // add in false northing if south of the equator
            }

            return new Coordinate(x, y);
        };
    }

    public static Function<LatLonCoord, Coordinate> getInstance(final Integer zone) {
        return getInstance(zone, Datum.WGS84);
    }

    public static Function<Coordinate, LatLonCoord> getInverseInstance(final Integer zone, final Datum datum, boolean southern) {
        final Integer utmz = zone;
        final Datum d = datum;
        final boolean south = southern;
        final Integer zcm = 3 + 6 * (utmz - 1) - 180;

        // constants taken from or calculated from the datum
        final double a = d.eqRad;
        final double f = 1.0 / d.flat;
        final double b = a * (1 - f);   // polar radius
        final double esq = (1 - Math.pow((b / a), 2));    // e-squared for use in expansions
        final double e = Math.sqrt(esq);                  // eccentricity
        final double e0 = e / Math.sqrt(1 - e);           // Called e prime in reference
        final double e0sq = e * e / (1.0 - Math.pow(e, 2));     // squared - always even powers
        final double e1 = (1 - Math.sqrt(1 - Math.pow(e, 2))) / (1 + Math.sqrt(1 - Math.pow(e, 2)));

        // constants used in calculations
        final double k = 1.0;
        final double k0 = 0.9996;
        final double drad = Math.PI / 180.0;

        return (Coordinate c) -> {
            final double x = c.getX();
            final double y = c.getY();

            final double M0 = 0.0;
            double M;

            if (!south) {
                M = M0 + y / k0;    // Arc length along standard meridian.
            } else {
                M = M0 + (y - 10000000) / k;
            }
            final double mu = M / (a * (1 - esq * (0.25 + esq * (3 / 64.0 + 5 * esq / 256.0))));
            double phi1 = mu + e1 * (1.5 - 27.0 * e1 * e1 / 32.0) * Math.sin(2 * mu) + e1 * e1 * (21.0 / 16.0 - 55.0 * e1 * e1 / 32.0) * Math.sin(4 * mu);   //Footprint Latitude
            phi1 = phi1 + e1 * e1 * e1 * (Math.sin(6.0 * mu) * 151.0 / 96.0 + e1 * Math.sin(8.0 * mu) * 1097.0 / 512.0);
            final double C1 = e0sq * Math.pow(Math.cos(phi1), 2);
            final double T1 = Math.pow(Math.tan(phi1), 2);
            final double N1 = a / Math.sqrt(1 - Math.pow(e * Math.sin(phi1), 2));
            final double R1 = N1 * (1 - Math.pow(e, 2)) / (1 - Math.pow(e * Math.sin(phi1), 2));
            final double D = (x - 500000) / (N1 * k0);
            double phi = (D * D) * (0.5 - D * D * (5.0 + 3.0 * T1 + 10 * C1 - 4.0 * C1 * C1 - 9 * e0sq) / 24.0);
            phi = phi + Math.pow(D, 6) * (61.0 + 90.0 * T1 + 298.0 * C1 + 45.0 * T1 * T1 - 252.0 * e0sq - 3.0 * C1 * C1) / 720.0;
            phi = phi1 - (N1 * Math.tan(phi1) / R1) * phi;

            final double lat = Math.floor(1000000 * phi / drad) / 1000000.0;
            double lng = D * (1.0 + D * D * ((-1.0 - 2.0 * T1 - C1) / 6.0 + D * D * (5.0 - 2.0 * C1 + 28.0 * T1 - 3.0 * C1 * C1 + 8.0 * e0sq + 24.0 * T1 * T1) / 120.0)) / Math.cos(phi1);
            lng = zcm + lng / drad;

            return new LatLonCoord(lat, lng);
        };
    }

    public enum Datum {
        WGS84(6378137.0, 298.2572236), // WGS 84
        NAD83(6378137.0, 298.2572236), // NAD 83
        GRS80(6378137.0, 298.2572215), // GRS 80
        WGS72(6378135.0, 298.2597208), // WGS 72
        AUS65(6378160.0, 298.2497323), // Austrailian 1965
        Kras1940(6378245.0, 298.2997381), // Krasovsky 1940
        NAmer1927(6378206.4, 294.9786982), // North American 1927
        Intl1924(6378388.0, 296.9993621), // International 1924
        Hayford1909(6378388.0, 296.9993621), // Hayford 1909
        Clarke1880(6378249.1, 293.4660167), // Clarke 1880
        Clarke1866(6378206.4, 294.9786982), // Clarke 1866
        Airy1830(6377563.4, 299.3247788), // Airy 1830
        Bessel1841(6377397.2, 299.1527052), // Bessel 1841
        Everset1830(6377276.3, 300.8021499); // Everest 1830

        public final double eqRad;
        public final double flat;

        private Datum(double eqRad, double flat) {
            this.eqRad = eqRad;
            this.flat = flat;
        }
    }
}
