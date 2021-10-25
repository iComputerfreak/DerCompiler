package de.dercompiler.io.message;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.Attribute;

import java.awt.*;
import java.util.Objects;

/**
 * Responsible for colorizing messages with 24-bit colors
 */
public class Colorizer24Bit implements IColorizer {
    
    @Override
    public String colorize(Color foreground, String message) {
        return colorize(foreground, null, message);
    }

    @Override
    public String colorize(Color foreground, Color background, String message) {
        Attribute fgc = null;
        Attribute bgc = null;

        if (!Objects.isNull(foreground)) {
            fgc = Attribute.TEXT_COLOR(foreground.getRed(), foreground.getGreen(), foreground.getBlue());
        }
        if (!Objects.isNull(background)) {
            bgc = Attribute.BACK_COLOR(background.getRed(), background.getGreen(), background.getBlue());
        }
        return Ansi.colorize(message, ColorizationHelper.removeInvalid(fgc, bgc));
    }
}
