/*
 * Copyright 2017 tauTerra, LLC.
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
 */
package com.tauterra.geo;

import java.util.Collection;

/**
 *
 * @author Nicholas Folse
 */
public class Util {

    public static final double PI_4 = Math.PI * 0.25;
    public static final double PI2 = Math.PI * 2;

    public static double deg2rad(double deg) {
        return deg * Math.PI / 180.0;
    }

    public static double rad2deg(double rad) {
        return rad * 180.0 / Math.PI;
    }

    /**
     * Compute the bearing from point "a" to point "b".
     *
     * @param a
     * @param b
     * @return bearing
     */
    public static double computeBearing(LatLonCoord a, LatLonCoord b) {
        double long1 = deg2rad(a.getLon()), lat1 = deg2rad(a.getLat()),
                long2 = deg2rad(b.getLon()), lat2 = deg2rad(b.getLat());

        double dLong = (long2 - long1);
        if (Math.abs(dLong) > Math.PI) {
            if (dLong > 0) {
                dLong = -(PI2 - dLong);
            } else {
                dLong += PI2;
            }
        }
        double dPhi = Math.log(Math.tan((lat2 * 0.5) + PI_4) / Math.tan((lat1 * 0.5) + PI_4));

        double brng = (rad2deg(Math.atan2(dLong, dPhi)) + 360) % 360.0;
        return brng;
    }

    public static LatLonCoord geoCentroid(Collection<LatLonCoord> coords) {
        final double[] sums = new double[]{
            0, // lat
            0}; // lon
        coords.stream().forEach(coord -> {
            sums[0] += coord.getLat();
            sums[1] += coord.getLon();
        });
        return new LatLonCoord(
                sums[0] / (double) coords.size(),
                sums[1] / (double) coords.size()
        );
    }
    
    

}
