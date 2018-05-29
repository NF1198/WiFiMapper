/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui.app;

import com.tauterra.geo.LatLonBounds;
import com.tauterra.geo.LatLonCoord;
import java.text.MessageFormat;

/**
 *
 * @author nickfolse
 */
public class NetworkWiFiAnalyzerJSHelper {

    private final NetworkWiFiAnalyzer analy;

    public NetworkWiFiAnalyzerJSHelper(NetworkWiFiAnalyzer analy) {
        this.analy = analy;
    }

    public String formatBounds(LatLonBounds b) {
        return MessageFormat.format(""
                + "'{'"
                + "east: {0,number,0.00000000}, "
                + "north: {1,number,0.00000000}, "
                + "south: {2,number,0.00000000}, "
                + "west: {3,number,0.00000000}"
                + "'}'",
                b.getMax().getLon(),
                b.getMax().getLat(),
                b.getMin().getLat(),
                b.getMin().getLon()
        );
    }

    /**
     * Generate javascript code to set the bounds of the specified map variable.
     *
     * @param mapVar
     * @param padding
     * @return
     */
    public String setBounds(String mapVar, double padding) {
        return MessageFormat.format("{0}.fitBounds({1}, {2,number,0.000000});",
                mapVar,
                formatBounds(analy.bounds()),
                padding
        );
    }

    public String setDataGeoJSON(String mapVar, String geoJson) {
        return MessageFormat.format(""
                + "(function() '{'\n"
                + "var geoJson = {1};\n"
                + "{0}.data.forEach((f)=>{0}.data.remove(f));\n"
                + "{0}.data.addGeoJson(geoJson);\n"
                + "{0}.data.setStyle((f) => '{'\n"
                + "return '{'\n"
                + "  fillColor: f.getProperty(\"fill\"),\n"
                + "  fillOpacity: f.getProperty(\"fill-opacity\"),\n"
                + "  strokeWeight: f.getProperty(\"stroke-width\"),\n"
                + "  strokeColor: f.getProperty(\"stroke\"),\n"
                + "  strokeOpacity: f.getProperty(\"stroke-opacity\"),\n"
                + "  title: f.getProperty(\"level\"),\n"
                + "  cursor: \"pointer\"\n"
                + "'}'\n"
                + "'}');\n"
                + "'}')();",
                 mapVar, geoJson);
    }

}
