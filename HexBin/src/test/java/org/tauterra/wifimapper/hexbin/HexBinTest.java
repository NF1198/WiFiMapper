/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.hexbin;

import java.util.Arrays;
import java.util.stream.Collectors;
import org.junit.Test;
import org.tauterra.wifimapper.hexbin.HexBin.CoordinateValue;

/**
 *
 * @author nickfolse
 */
public class HexBinTest {

    public HexBinTest() {
    }

    /**
     * Test of bins method, of class HexBin.
     */
    @Test
    public void testHexBin() {
        System.out.println("HexBin Test 1");
        double[][] data = new double[][]{
            new double[]{0.5, 0.5, 20.0},
            new double[]{0.5, 0.5, 30.0},
            new double[]{0.5, 0.5, 40.0},
            new double[]{0.5, 0.5, 10.0},
            new double[]{1.5, 1.5, 20.0},
            new double[]{1.5, 1.5, 20.0},
            new double[]{1.5, 1.5, 20.0}};
        HexBin hexbin = new HexBin(Arrays.asList(data)
                .stream()
                .map(d -> new CoordinateValue(d[0], d[1], d[2]))
                .collect(Collectors.toList()),
                1);
        hexbin.bins().forEach(System.out::println);
    }

    /**
     * Test of bins method, of class HexBin.
     */
    @Test
    public void testHexBin2() {
        System.out.println("HexBin Test 2");
        double[][] data = new double[][]{
            new double[]{0, 0, 10.0},
            new double[]{0, 1, 10.0},
            new double[]{0, 2, 10.0},
            new double[]{1, 0, 10.0},
            new double[]{1, 1, 10.0},
            new double[]{1, 2, 10.0},
            new double[]{2, 0, 10.0},
            new double[]{2, 1, 10.0},
            new double[]{2, 2, 10.0}};
        HexBin hexbin = new HexBin(Arrays.asList(data)
                .stream()
                .map(d -> new CoordinateValue(d[0], d[1], d[2]))
                .collect(Collectors.toList()),
                1);
        hexbin.bins().forEach(System.out::println);
    }
    
    /**
     * Test of bins method, of class HexBin.
     */
    @Test
    public void testHexBin3() {
        System.out.println("HexBin Test 3");
        double[][] data = new double[][]{
            new double[]{0, 0, 10.0},
            new double[]{0, 1, 10.0},
            new double[]{0, 2, 10.0},
            new double[]{1, 0, 10.0},
            new double[]{1, 1, 10.0},
            new double[]{1, 2, 10.0},
            new double[]{2, 0, 10.0},
            new double[]{2, 1, 10.0},
            new double[]{2, 2, 10.0}};
        HexBin hexbin = new HexBin(Arrays.asList(data)
                .stream()
                .map(d -> new CoordinateValue(d[0], d[1], d[2]))
                .collect(Collectors.toList()),
                2);
        hexbin.bins().forEach(System.out::println);
    }

}
