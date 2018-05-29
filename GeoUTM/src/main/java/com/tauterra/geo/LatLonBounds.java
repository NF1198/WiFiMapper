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

import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;

/**
 *
 * @author Nicholas Folse
 */
public class LatLonBounds {

    final LatLonCoord min, max;

    public LatLonBounds(LatLonCoord min, LatLonCoord max) {
        this.min = min;
        this.max = max;
    }

    public LatLonCoord getMin() {
        return min;
    }

    public LatLonCoord getMax() {
        return max;
    }

    public LatLonBounds extend(LatLonBounds other) {
        final double minLat = Math.min(this.min.getLat(), other.min.getLat());
        final double minLon = Math.min(this.min.getLon(), other.min.getLon());
        final double maxLat = Math.max(this.max.getLat(), other.max.getLat());
        final double maxLon = Math.max(this.max.getLon(), other.max.getLon());
        return new LatLonBounds(
                new LatLonCoord(minLat, minLon),
                new LatLonCoord(maxLat, maxLon)
        );
    }

    public static LatLonBounds fromBounds(Collection<LatLonBounds> bounds) {
        if (bounds.isEmpty()) {
            return null;
        }
        if (bounds.size() == 1) {
            return bounds.iterator().next();
        }
        LatLonBounds result = bounds.iterator().next();
        for (LatLonBounds bound : bounds) {
            result = result.extend(bound);
        }
        return result;
    }

    public static LatLonBounds from(Collection<LatLonCoord> coords) {
        if (coords.isEmpty()) {
            return null;
        }
        final Double[] bounds = new Double[]{
            Double.NaN, // minLat
            Double.NaN, // minLon
            Double.NaN, // maxLat
            Double.NaN}; // maxLon
        coords.stream().forEach(coord -> {
            bounds[0] = Double.isNaN(bounds[0]) ? coord.getLat() : Math.min(bounds[0], coord.getLat());
            bounds[1] = Double.isNaN(bounds[1]) ? coord.getLon() : Math.min(bounds[1], coord.getLon());
            bounds[2] = Double.isNaN(bounds[2]) ? coord.getLat() : Math.max(bounds[2], coord.getLat());
            bounds[3] = Double.isNaN(bounds[3]) ? coord.getLon() : Math.max(bounds[3], coord.getLon());
        });
        return new LatLonBounds(
                new LatLonCoord(bounds[0], bounds[1]),
                new LatLonCoord(bounds[2], bounds[3])
        );
    }

    @Override
    public int hashCode() {
        int hash = 7;
        hash = 67 * hash + Objects.hashCode(this.min);
        hash = 67 * hash + Objects.hashCode(this.max);
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final LatLonBounds other = (LatLonBounds) obj;
        if (!Objects.equals(this.min, other.min)) {
            return false;
        }
        if (!Objects.equals(this.max, other.max)) {
            return false;
        }
        return true;
    }

    @Override
    public String toString() {
        return "LatLonBounds{" + "min=" + min + ", max=" + max + '}';
    }

}
