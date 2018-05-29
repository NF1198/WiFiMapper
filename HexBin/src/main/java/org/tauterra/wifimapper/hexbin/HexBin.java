/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.hexbin;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 *
 * @author nickfolse
 */
public class HexBin {

    private static final double THIRD_PI = Math.PI / 3.0;
    private static final double[] ANGLES = new double[]{
        0, THIRD_PI, 2 * THIRD_PI, 3 * THIRD_PI, 4 * THIRD_PI, 5 * THIRD_PI
    };

    private final Map<HexCoord, Bin> binsById = new HashMap<>();

    private double r;
    private double dx, dy;
    private double x0, y0, x1, y1;

    public double r() {
        return r;
    }

    private HexBin r(double value) {
        this.r = value;
        this.dx = r * 2 * Math.sin(THIRD_PI);
        this.dy = r * 1.5;
        return this;
    }

    public HexBin(List<CoordinateValue> coords, double r) {

        r(r);

        for (CoordinateValue coord : coords) {
            double px = coord.x;
            double py = coord.y;
            if (Double.isNaN(px) || Double.isNaN(py)) {
                continue;
            }

            py /= dy;
            long pj = Math.round(py);
            px /= dx;
            px -= (pj & 1L) / 2.0;
            long pi = Math.round(px);
            double py1 = py - pj;

            if (Math.abs(py1) * 3 > 1) {
                double px1 = px - pi;
                long pi2 = pi + (px < pi ? -1 : 1) / 2L;
                long pj2 = pj + (py < pj ? -1 : 1);
                double px2 = px - pi2;
                double py2 = py - pj2;
                if (px1 * px1 + py1 * py1 > px2 * px2 + py2 * py2) {
                    pi = pi2 + ((pj & 1L) == 1 ? 1 : -1) / 2;
                    pj = pj2;
                }
            }

            HexCoord hexCoord = new HexCoord(pi, pj);
            Bin b;
            if (binsById.containsKey(hexCoord)) {
                b = binsById.get(hexCoord);
            } else {
                double binx = (pi + (pj & 1) / 2.0) * dx;
                double biny = pj * dy;
                b = new Bin(binx, biny, r);
                binsById.put(hexCoord, b);
            }
            b.values().add(coord.value);

        }
    }

    public Collection<Bin> bins() {
        return binsById.values();
    }

    /**
     * Generate a generic hexagon path based on the specified radius
     *
     * @param radius
     * @return
     */
    private static double[][] hexagon_(double radius) {
        double _x0 = 0, _y0 = 0;
        double _r = radius;
        double[][] result = new double[ANGLES.length][];
        for (int idx = 0; idx < result.length; idx++) {
            double angle = ANGLES[idx];
            double _x1 = Math.sin(angle) * _r;
            double _y1 = -Math.cos(angle) * _r;
            _x0 = _x1;
            _y0 = _y1;
            result[idx] = new double[]{_x1, _y1};
        }
        return result;
    }

    /**
     * Return a generic hexagon path based on the specified radius
     *
     * @param radius
     * @return
     */
    public static double[][] hexagon(double radius) {
        return hexagon_(radius);
    }

    private static class HexCoord {

        private final long pi, pj;

        public HexCoord(long pi, long pj) {
            this.pi = pi;
            this.pj = pj;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 37 * hash + (int) (this.pi ^ (this.pi >>> 32));
            hash = 37 * hash + (int) (this.pj ^ (this.pj >>> 32));
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
            final HexCoord other = (HexCoord) obj;
            if (this.pi != other.pi) {
                return false;
            }
            if (this.pj != other.pj) {
                return false;
            }
            return true;
        }

    }

    public static class Bin {

        private final double centerX, centerY;
        private final double size;
        private final List<Double> values = new ArrayList<>();

        public Bin(double centerX, double centerY, double size) {
            this.centerX = centerX;
            this.centerY = centerY;
            this.size = size;
        }

        public List<Double> values() {
            return values;
        }

        public double getCenterX() {
            return centerX;
        }

        public double getCenterY() {
            return centerY;
        }

        public double getSize() {
            return size;
        }
        
        public int count() {
            return values.size();
        }
        
        public double sum() {
            return values.stream().collect(Collectors.summingDouble(d -> d));
        }
        
        public double average() {
            return values.stream().collect(Collectors.averagingDouble(d -> d));
        }
        

        @Override
        public String toString() {
            return MessageFormat.format("Bin'{'x: {0}, y: {1}, size: {2}, count: {3}, sum: {4}, mean: {5}'}'", centerX, centerY, size, count(), sum(), average());
        }

    }

    public static class CoordinateValue {

        public final double x, y, value;

        public CoordinateValue(double x, double y, double value) {
            this.x = x;
            this.y = y;
            this.value = value;
        }

    }

}
