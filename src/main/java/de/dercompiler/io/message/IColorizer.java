package de.dercompiler.io.message;

import java.awt.*;

/**
 * IColorizer defines an interface for the different color formats
 */
public interface IColorizer {

    /**
     * Colorizes the message based on the color system implemented
     *
     * @param foreground The foreground color
     * @param message The message
     * @return The colorized message
     */
    String colorize(Color foreground, String message);

    /**
     * Colorizes the message based on the color system implemented
     *
     * @param foreground The foreground color
     * @param background The background color
     * @param message The message
     * @return The colorized message
     */
    String colorize(Color foreground, Color background, String message);
}
