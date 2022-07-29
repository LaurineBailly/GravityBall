package com.example.gravityball.modele;

import android.util.DisplayMetrics;
import android.util.Log;

// Warning : configure(DisplayMetrics displayMetrics) needs to be called on class before using
public abstract class PixelSizer {

    // Mm corresponding to one pixel
    private static double pixelsInOneMm;

    public PixelSizer() {
    }

    // Determining the dimension in mm of 1 pixel.
    public static void configure(DisplayMetrics displayMetrics) {

        // getResources().getDisplayMetrics().density is the scaling factor for the Density
        // Independent Pixel unit, where one DIP is one pixel on an approximately 160 dpi screen
        // density of the screen (pixels per inch). DENSITY_DEFAULT = 160.
        // getting the number of pixels in 1 inch
        double density = displayMetrics.DENSITY_DEFAULT*displayMetrics.density;

        // 25.4mm = 1 inch.
        // Getting the number of pixels in 1mm.
        pixelsInOneMm = density/25.4;
    }

    // Pixels to mm converter
    public static double convertPixelsToMillimeters(double pixels) {
        if(pixelsInOneMm == 0) {
            return 0;
        }
        else {
            return pixels/pixelsInOneMm;
        }
    }

    // Meters to pixels converter
    public static double convertMetersToPixels(double meters) {
        return meters*1000*pixelsInOneMm;

    }
}
