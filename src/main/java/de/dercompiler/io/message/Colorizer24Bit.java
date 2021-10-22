package de.dercompiler.io.message;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.awt.*;

public class Colorizer24Bit implements IColorizer {
    @Override
    public String colorize(Color foreground, String message) {
        Attribute fgc = Attribute.TEXT_COLOR(foreground.getRed(), foreground.getGreen(), foreground.getBlue());
        return Ansi.colorize(message, fgc);
    }

    @Override
    public String colorize(Color foreground, Color background, String message) {
        Attribute fgc = Attribute.TEXT_COLOR(foreground.getRed(), foreground.getGreen(), foreground.getBlue());
        Attribute bgc = Attribute.BACK_COLOR(background.getRed(), background.getGreen(), background.getBlue());
        return Ansi.colorize(message, fgc, bgc);
    }
}
