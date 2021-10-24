package de.dercompiler.io.message;

import java.awt.*;

/**
 * IColorizer defines a Interface for the different ColorFormats
 */
public interface IColorizer {

    /**
     * colorizes the message based on the color system implemented
     *
     * @param foreground the foreground color
     * @param message the message
     * @return the colorized message
     */
    String colorize(Color foreground, String message);

    /**
     * colorizes the message based on the color system implemented
     *
     * @param foreground the foreground color
     * @param background the background color
     * @param message the message
     * @return the colorized message
     */
    String colorize(Color foreground, Color background, String message);
}
