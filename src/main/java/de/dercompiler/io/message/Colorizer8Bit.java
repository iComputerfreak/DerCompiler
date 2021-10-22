package de.dercompiler.io.message;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.awt.*;
import java.util.Objects;

public class Colorizer8Bit implements IColorizer {

    private static final double FACTOR = 256 / 6;

    private int convertChannel(int color) {
        return (int) (color / FACTOR);
    }

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
