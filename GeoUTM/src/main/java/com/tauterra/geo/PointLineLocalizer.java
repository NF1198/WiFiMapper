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

import java.util.List;

/**
 *
 * @author Nicholas Folse
 */
public class PointLineLocalizer {

    public static LocalizerResult Localize(Coordinate point, List<Coordinate> line) {
        if (line.size() < 2) {
            return null;
        }
        double minDist = Double.MAX_VALUE;
        LocalizerResult bestResult = new LocalizerResult(Double.NaN, Double.NaN, point);
        int minIndex = 0;
        Coordinate a = line.get(0);
        for (int idx = 1; idx < line.size(); idx++) {
            final Coordinate b = line.get(idx);
            LocalizerResult localResult = Localize(a, b, point);
            if (localResult != null) {
                final double t = localResult.getT();
                final double absD = Math.abs(localResult.d);
                if (0.0 <= t && t <= 1.0 && absD < minDist) {
                    minDist = absD;
                    minIndex = idx - 1;
                    bestResult = localResult;
                }
                a = b;
            }
        }
        return new LocalizerResult(bestResult.getT() + minIndex, bestResult.getD(), bestResult.getIntersection());
    }

    /**
     *
     * @param a line end-point
     * @param b line end-point
     * @param w point to test
     * @return {object or null} Returns null if (a == b)
     */
    public static LocalizerResult Localize(Coordinate a, Coordinate b, Coordinate w) {
        double ax = a.getX(), ay = a.getY();
        double bx = b.getX(), by = b.getY();
        double wx = w.getX(), wy = w.getY();

        if (ax == bx && ay == by) {
            if (ax == wx && ay == wy) {
                return new LocalizerResult(1, 0, a);
            } else {
                return null;
            }
        } else if (ax == wx && ay == wy) {
            return new LocalizerResult(0, 0, a);
        } else if (bx == wx && by == wy) {
            return new LocalizerResult(1, 0, b);
        }

        double denom = Math.pow(bx - ax, 2.0) + Math.pow(by - ay, 2.0);

        double t = ((wx - ax) * (bx - ax) + (wy - ay) * (by - ay)) / (denom);

        double ix = ax + t * (bx - ax);
        double iy = ay + t * (by - ay);

        double dist = ((by - ay) * wx - (bx - ax) * wy + bx * ay - by * ax) / (Math.sqrt(denom));

        return new LocalizerResult(t, dist, new Coordinate(ix, iy));
    }

    public static class LocalizerResult {

        private final double t;
        private final double d;
        private final Coordinate intersection;

        public LocalizerResult(double t, double d, Coordinate intersection) {
            this.t = t;
            this.d = d;
            this.intersection = intersection;
        }

        /**
         * fractional distance between a and b
         *
         * @return
         */
        public double getT() {
            return t;
        }

        /**
         * distance from point of intersection to test point (d > 0) ==> d is to
         * the right of a-b (d < 0) ==> d is to the left of a-b
         *
         * @return
         */
        public double getD() {
            return d;
        }

        public Coordinate getIntersection() {
            return intersection;
        }

        @Override
        public String toString() {
            return "LocalizerResult{" + "t=" + t + ", d=" + d + ", intersection=" + intersection + '}';
        }

    }

}
