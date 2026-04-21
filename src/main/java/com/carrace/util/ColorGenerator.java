package com.carrace.util;

import java.util.Random;

public class ColorGenerator {
    
    private static final Random random = new Random();
    
    /**
     * Generates a random hex color code
     * @return hex color string in format #RRGGBB
     */
    public static String generateRandomHex() {
        int r = random.nextInt(256);
        int g = random.nextInt(256);
        int b = random.nextInt(256);
        return String.format("#%02X%02X%02X", r, g, b);
    }
    
    /**
     * Generates a vibrant random hex color (high saturation)
     * @return hex color string in format #RRGGBB
     */
    public static String generateVibrantHex() {
        int primaryIndex = random.nextInt(3);
        int[] rgb = new int[3];
        
        // One channel is max, others are lower
        rgb[primaryIndex] = 200 + random.nextInt(56); // 200-255
        
        for (int i = 0; i < 3; i++) {
            if (i != primaryIndex) {
                rgb[i] = random.nextInt(150); // 0-149 for contrast
            }
        }
        
        return String.format("#%02X%02X%02X", rgb[0], rgb[1], rgb[2]);
    }
}
