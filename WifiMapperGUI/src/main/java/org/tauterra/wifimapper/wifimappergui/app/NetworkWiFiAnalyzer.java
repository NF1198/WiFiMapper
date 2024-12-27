/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui.app;

import com.tauterra.geo.Coordinate;
import com.tauterra.geo.LatLonBounds;
import com.tauterra.geo.LatLonCoord;
import com.tauterra.geo.projections.GeoUTM;
import com.tauterra.geo.projections.GeoUTM.Datum;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.function.DoubleFunction;
import java.util.function.Function;
import java.util.stream.Collectors;
import org.tauterra.wifimapper.hexbin.HexBin;
import org.tauterra.wifimapper.hexbin.HexBin.Bin;
import org.tauterra.wifimapper.hexbin.HexBin.CoordinateValue;
import org.tauterra.wifimapper.wifimappergui.ColorGradient;
import org.tauterra.wifimapper.wifimappergui.data.Location;
import org.tauterra.wifimapper.wifimappergui.data.Network;

/**
 *
 * @author nickfolse
 */
public final class NetworkWiFiAnalyzer {

    private final Network net;
    private final double hexSize;
    private Function<LatLonCoord, Coordinate> projection = null;
    private Function<Coordinate, LatLonCoord> invProjection = null;
    private HexBin hexBin = null;

    public NetworkWiFiAnalyzer(Network net, double hexSize) {
        this.net = net;
        this.hexSize = hexSize;
        analyze();
    }

    public NetworkWiFiAnalyzer analyze() {
        projectLocations(net.getLocations());
        hexBin = new HexBin(net.getLocations().stream()
                .map(l -> new CoordinateValue(l.getLocalCoord().getX(), l.getLocalCoord().getY(), l.getLevel()))
                .collect(Collectors.toList()), hexSize);
        return this;
    }

    public String geoJSON() {
        double[][] hexagon = HexBin.hexagon(hexBin.r());
        MessageFormat featureFormat = new MessageFormat("'{'\n"
                + "            \"type\": \"Feature\",\n"
                + "            \"geometry\": '{'\n"
                + "                \"type\": \"Polygon\",\n"
                + "                \"coordinates\": [[\n"
                + "                  [{0,number,0.00000000}, {1,number,0.00000000}],\n"
                + "                  [{2,number,0.00000000}, {3,number,0.00000000}],\n"
                + "                  [{4,number,0.00000000}, {5,number,0.00000000}],\n"
                + "                  [{6,number,0.00000000}, {7,number,0.00000000}],\n"
                + "                  [{8,number,0.00000000}, {9,number,0.00000000}],\n"
                + "                  [{10,number,0.00000000}, {11,number,0.00000000}],\n"
                + "                  [{12,number,0.00000000}, {13,number,0.00000000}]\n"
                + "            ]]'}',\n"
                + "            \"properties\": '{'\n"
                + "                \"level\": {14},\n"
                + "                \"fill\": \"{15}\",\n"
                + "                \"fill-opacity\": {16},\n"
                + "                \"stroke-width\": {17},\n"
                + "                \"stroke\": \"{18}\",\n"
                + "                \"stroke-opacity\": {19},\n"
                + "                \"lat\": {20,number,0.00000000},\n"
                + "                \"lon\": {21,number,0.00000000}\n"
                + "            '}'\n"
                + "        '}'");
        List<String> features = new ArrayList<>();

        double min = Double.POSITIVE_INFINITY;
        double max = Double.NEGATIVE_INFINITY;
        Bin maxBin = null;
        for (Bin b : hexBin().bins()) {
            maxBin = maxBin == null ? b : maxBin;
            double level = b.average();
            min = Math.min(level, min);
            max = Math.max(level, max);
            if (Double.compare(level, max) == 0) {
                maxBin = b;
            }
        }
        max = Math.max(max, -35);
        min = Math.min(min, -95);

        DoubleFunction<String> colorGradient = ColorGradient.RedGreenHex(min, max);

        for (Bin b : hexBin().bins()) {
            if (b.count() == 0) {
                continue;
            }
            double cx = b.getCenterX();
            double cy = b.getCenterY();
            List<LatLonCoord> latLons = new ArrayList<>(6);
            for (int idx = 0; idx < 6; idx++) {
                latLons.add(invProjection.apply(new Coordinate(hexagon[idx][0] + cx, hexagon[idx][1] + cy)));
            }

            double levelForColor = (b != maxBin) ? b.average() : 9999;
            LatLonCoord center = invProjection.apply(new Coordinate(cx, cy));
            features.add(featureFormat.format(new Object[]{
                latLons.get(0).getLon(), latLons.get(0).getLat(),
                latLons.get(1).getLon(), latLons.get(1).getLat(),
                latLons.get(2).getLon(), latLons.get(2).getLat(),
                latLons.get(3).getLon(), latLons.get(3).getLat(),
                latLons.get(4).getLon(), latLons.get(4).getLat(),
                latLons.get(5).getLon(), latLons.get(5).getLat(),
                latLons.get(0).getLon(), latLons.get(0).getLat(),
                b.average(),
                colorGradient.apply(levelForColor),
                0.66,
                0.5,
                colorGradient.apply(levelForColor),
                0.9,
                center.getLat(),
                center.getLon()
            }));
        }
        StringBuilder result = new StringBuilder();
        result.append("{\n"
                + "  \"type\": \"FeatureCollection\",\n"
                + "  \"features\": [");
        result.append(String.join(",", features));
        result.append("]\n"
                + "}");
        return result.toString();
    }

    public void projectLocations(List<Location> locations) {
        if (projection == null) {
            establishProjection();
        }
        for (Location l : locations) {
            LatLonCoord coord = new LatLonCoord(l.getLat(), l.getLon());
            l.setLocalCoord(getProjection().apply(coord));
        }
    }

    public Network net() {
        return net;
    }

    public LatLonBounds bounds() {
        return LatLonBounds.from(hexBin().bins().stream()
                .map((b) -> invProjection.apply(new Coordinate(b.getCenterX(), b.getCenterY())))
                .collect(Collectors.toList()));
    }

    private void establishProjection() {
        Location loc = net.getLocations().get(0);
        LatLonCoord coord = new LatLonCoord(loc.getLat(), loc.getLon());
        Integer zone = GeoUTM.findUTMZone(coord);
        setProjection(GeoUTM.getInstance(zone, Datum.WGS84));
        boolean southern = loc.getLat() < 0;
        setInvProjection(GeoUTM.getInverseInstance(zone, GeoUTM.Datum.WGS84, southern));
    }

    public Function<LatLonCoord, Coordinate> getProjection() {
        return projection;
    }

    public void setProjection(Function<LatLonCoord, Coordinate> projection) {
        this.projection = projection;
    }

    public Function<Coordinate, LatLonCoord> getInvProjection() {
        return invProjection;
    }

    public void setInvProjection(Function<Coordinate, LatLonCoord> invProjection) {
        this.invProjection = invProjection;
    }

    public HexBin hexBin() {
        return hexBin;
    }

}
