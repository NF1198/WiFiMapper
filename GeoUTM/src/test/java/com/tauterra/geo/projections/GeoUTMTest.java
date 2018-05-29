package com.tauterra.geo.projections;

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


import com.tauterra.geo.LatLonCoord;
import com.tauterra.geo.Coordinate;
import com.tauterra.geo.projections.GeoUTM.Datum;
import java.util.function.Function;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Nicholas Folse
 */
public class GeoUTMTest {

    public GeoUTMTest() {
    }

    public static final LatLonCoord homeCoord = new LatLonCoord(35.101895, -106.568790);
    public static final Coordinate homeUTM = new Coordinate(357016.9, 3885468.7);

    /**
     * Test of findUTMZone method, of class GeoUTM.
     */
    @Test
    public void testFindUTMZone() {
        System.out.println("GeoUTMTest:findUTMZone");
        LatLonCoord latLon = homeCoord;
        Integer expResult = 13;
        Integer result = GeoUTM.findUTMZone(latLon);
        assertEquals(expResult, result);
    }

    /**
     * Test of getInstance method, of class GeoUTM.
     */
    /**
     * Test of getInstance method, of class GeoUTM.
     */
    @Test
    public void testGetInstance() {
        System.out.println("GeoUTMTest:projection");
        Integer zone = GeoUTM.findUTMZone(homeCoord);
        Function<LatLonCoord, Coordinate> projection = GeoUTM.getInstance(zone);
        Coordinate result = projection.apply(homeCoord);
        System.out.println(result);
        Coordinate expResult = homeUTM;
        System.out.println(expResult);
        Double dEps = expResult.distance(result);
        System.out.println("dEps: " + dEps);
        assertTrue(expResult.distance(result) < 0.01);
    }

    /**
     * Test of getInverseInstance method, of class GeoUTM.
     */
    @Test
    public void testGetInverseInstance() {
        System.out.println("GeoUTMTest:inverseProjection");
        Integer zone = GeoUTM.findUTMZone(homeCoord);
        Function<Coordinate, LatLonCoord> projection = GeoUTM.getInverseInstance(zone, Datum.WGS84, false);
        LatLonCoord result = projection.apply(homeUTM);
        System.out.println(result);
        LatLonCoord expResult = homeCoord;
        System.out.println(expResult);
        Double dEps = expResult.distance(result);
        System.out.println("dEps: " + dEps);
        assertTrue(expResult.distance(result) < 0.0001);
    }

}
