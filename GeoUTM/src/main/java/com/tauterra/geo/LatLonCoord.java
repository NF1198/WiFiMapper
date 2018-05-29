/*
 * Copyright 2017 .
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

/**
 *
 * @author Nicholas Folse
 */
public class LatLonCoord extends Coordinate {

    public LatLonCoord(Double lat, Double lon) {
        super(lon, lat);
    }

    public static LatLonCoord of(String lat, String lon) {
        Double latitude = Double.parseDouble(lat);
        Double longitude = Double.parseDouble(lon);
        return new LatLonCoord(latitude, longitude);
    }

    public Double getLat() {
        return getY();
    }

    public Double getLon() {
        return getX();
    }

    @Override
    public String toString() {
        return "LatLonCoord{" + "lon: " + getLon() + ", lat=" + getLat() + '}';
    }
    
    
}
