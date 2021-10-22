package de.dercompiler.io.message;

import java.awt.*;

public class NoColorColorizer implements IColorizer {
    @Override
    public String colorize(Color foreground, String message) {
        return colorize(foreground, null, message);
    }

    @Override
    public String colorize(Color foreground, Color background, String message) {
        return message;
    }
}
