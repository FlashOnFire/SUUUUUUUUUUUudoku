package fr.polytech.suuuuuuuuuuudoku.graphics;

import java.util.HashMap;

/**
 * Utility class containing various utility methods.
 */
public class Utils {
    /**
     * Private constructor to prevent instantiation of the Utils class.
     */
    private Utils() {
    }

    /**
     * Converts HSL (Hue, Saturation, Lightness) color values to RGB (Red, Green, Blue) color values.
     *
     * @param h The hue value, in degrees (0-360).
     * @param s The saturation value, as a percentage (0-1).
     * @param l The lightness value, as a percentage (0-1).
     * @return An array containing the RGB values, each ranging from 0 to 255.
     */
    static public int[] hslToRgb(float h, float s, float l) {
        float c = (1 - Math.abs(2 * l - 1)) * s;
        float x = c * (1 - Math.abs((h / 60) % 2 - 1));
        float m = l - c / 2;
        float r = 0, g = 0, b = 0;

        if (0 <= h && h < 60) {
            r = c;
            g = x;
            b = 0;
        } else if (60 <= h && h < 120) {
            r = x;
            g = c;
            b = 0;
        } else if (120 <= h && h < 180) {
            r = 0;
            g = c;
            b = x;
        } else if (180 <= h && h < 240) {
            r = 0;
            g = x;
            b = c;
        } else if (240 <= h && h < 300) {
            r = x;
            g = 0;
            b = c;
        } else if (300 <= h && h < 360) {
            r = c;
            g = 0;
            b = x;
        }

        int[] rgb = new int[3];
        rgb[0] = Math.round((r + m) * 255);
        rgb[1] = Math.round((g + m) * 255);
        rgb[2] = Math.round((b + m) * 255);
        return rgb;
    }

    /**
     * Applies a mapping from the original 2D array to the mapped 2D array using the provided mapping.
     *
     * @param original The original 2D array.
     * @param mapped   The 2D array to store the mapped values.
     * @param mapping  The HashMap containing the mapping from original values to mapped values.
     * @param <O>      The type of the original values.
     * @param <M>      The type of the mapped values.
     */
    public static <O, M> void applyMapping(O[][] original, M[][] mapped, HashMap<O, M> mapping) {
        assert original.length == mapped.length;
        if (original.length == 0) return;
        assert original[0].length == mapped[0].length;

        for (int i = 0; i < original.length; i++) {
            for (int j = 0; j < original[0].length; j++) {
                mapped[i][j] = mapping.get(original[i][j]);
            }
        }

    }
}
