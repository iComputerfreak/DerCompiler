package de.dercompiler.io.message;

import java.awt.*;

public interface IColorizer {
    String colorize(Color foreground, String message);
    String colorize(Color foreground, Color background, String message);
}
