/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.tauterra.wifimapper.wigledb;

import java.io.File;
import java.net.URI;
import java.util.Optional;
import org.junit.Test;

/**
 *
 * @author nickfolse
 */
public class WigleDBOTest {

    public WigleDBOTest() {
    }

    /**
     * Test of close method, of class WigleDBO.
     *
     * @throws java.lang.Exception
     */
    @Test
    public void testWigleDBO() throws Exception {

        URI testDB = WigleDBOTest.class.getResource("/db/test.sqlite").toURI();

        try (WigleDBO wigle = WigleDBO.Open(new File(testDB))) {
            wigle.listNetworks().forEach(System.out::println);

            System.out.println("");
            System.out.println("00:22:aa:b8:b2:f8 details:");
            Optional<WigleNetwork> net = wigle.findNetwork("00:22:aa:b8:b2:f8");
            System.out.println(net.get());
            
            System.out.println("");
            System.out.println("00:22:aa:b8:b2:f8 locations");
            wigle.findLocations("00:22:aa:b8:b2:f8").forEach(System.out::println);

        } finally {
        }

    }

}
