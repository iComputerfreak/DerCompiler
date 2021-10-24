package de.dercompiler.io.message;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.awt.*;
import java.util.Objects;

/**
 * Converts true colors to 8-bit colors and colorizes messages with them
 */
public class Colorizer8Bit implements IColorizer {

    private static final double FACTOR = 256d / 6;

    /**
     * Converts a single channel
     * @param color The color to convert
     */
    private int convertChannel(int color) {
        return (int) (color / FACTOR);
    }
    
    /**
     * Converts a given color to a 8-bit color
     * @param color The color to convert
     * @return The 8-bit color
     */
    private int convert(Color color) {
        return 16 + 36 * convertChannel(color.getRed()) + 6 * convertChannel(color.getGreen()) + convertChannel(color.getBlue());
    }

    @Override
    public String colorize(Color foreground, String message) {
        return colorize(foreground, null, message);
    }

    @Override
    public String colorize(Color foreground, Color background, String message) {

        Attribute fgc = null;
        Attribute bgc = null;
        if (!Objects.isNull(foreground)) {
            fgc = Attribute.TEXT_COLOR(convert(foreground));
        }
        if (!Objects.isNull(background)) {
            bgc = Attribute.TEXT_COLOR(convert(background));
        }
        return Ansi.colorize(message, ColorizationHelper.removeInvalid(fgc, bgc));
    }
}
