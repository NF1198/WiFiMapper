/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package org.tauterra.wifimapper.wifimappergui;

import java.util.Arrays;
import java.util.List;
import java.util.function.DoubleFunction;
import javafx.scene.paint.Color;
import javafx.scene.paint.CycleMethod;
import javafx.scene.paint.LinearGradient;
import javafx.scene.paint.Stop;

/**
 *
 * @author nickfolse
 */
public class ColorGradient {

    private static final LinearGradient REDGREEN = new LinearGradient(0, 1, 0, 1, true, CycleMethod.REFLECT, Arrays.asList(
            new Stop(0, Color.RED),
            new Stop(0.25, Color.ORANGERED),
            new Stop(0.5, Color.YELLOWGREEN),
            new Stop(0.945, Color.GREEN),
            new Stop(0.95, Color.BLUE),
            new Stop(1, Color.BLUE)
    ));

    private static DoubleFunction<Double> UnMap(double from, double to) {
        return (value) -> (value - from) / (to - from);
    }

    public static DoubleFunction<Color> RedGreen(double from, double to) {
        final DoubleFunction<Double> unmpapper = UnMap(from, to);

        final List<Stop> stops = REDGREEN.getStops();
        
        return (value) -> {
            double t = unmpapper.apply(value);
            Color a = stops.get(0).getColor();
            Double w = stops.get(0).getOffset();
            for (Stop s : stops.subList(1, stops.size())) {
                Color b = s.getColor();
                Double v = s.getOffset();
                if (v > t) {
                    double sub_t = (t - w) / (v - w);
                    return a.interpolate(b, sub_t);
                }
                a = b;
                w = v;
            }
            return a;
        };

    }

    public static DoubleFunction<String> RedGreenHex(double from, double to) {
        DoubleFunction<Color> base = RedGreen(from, to);
        return (value) -> {
            Color c = base.apply(value);
            return String.format("#%02X%02X%02X",
                    (int) (c.getRed() * 255),
                    (int) (c.getGreen() * 255),
                    (int) (c.getBlue() * 255));
        };
    }

}
