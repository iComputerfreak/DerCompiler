package de.dercompiler.io.message;

import java.awt.*;

/**
 * The MessageOrigin describes the origin of the message and depending on this information generate different error and warning-codes.
 * Part of the MessageOrigin, is the color-flair of the messages
 */
public enum MessageOrigin {
    /** Common errors */
    GENERAL("COM", null, new Color(0x20,0x63,0x9B), new Color(0xF6,0xD5,0x5C), new Color(0xED,0x55,0x3B)),
    /** Errors in the lexer */
    LEXER("LEX", null, new Color(0, 0, 0), new Color(0,0,0), new Color(0,0,0)),
    /** Errors in the parser */
    PARSER("PAR", null, new Color(0, 0, 0), new Color(0,0,0), new Color(0,0,0))

    //add more origins
    ;

    private final String identifier;

    private final Color text;
    private final Color info;
    private final Color warning;
    private final Color error;

    /**
     * Creates a new MessageOrigin with the given data
     * @param identifier The identifier used for logging output
     * @param text The text color
     * @param info The info color
     * @param warning The warning color
     * @param error The error color
     */
    MessageOrigin(String identifier, Color text, Color info, Color warning, Color error) {
        this.identifier = identifier;

        this.text = text;
        this.info = info;
        this.warning = warning;
        this.error = error;
    }

    /**
     * @return The integer ID of this origin 
     */
    public int getId() {
        return this.ordinal();
    }

    /**
     * @return The logging identifier of this origin
     */
    public String getIdentifier() {
        return identifier;
    }

    /**
     * @return The text color of this origin
     */
    public Color getTextColor() {
        return text;
    }

    /**
     * @return The info color of this origin
     */
    public Color getInfoColor() {
        return info;
    }

    /**
     * @return The warning color of this origin
     */
    public Color getWarningColor() {
        return warning;
    }

    /**
     * @return The error color of this origin
     */
    public Color getErrorColor() {
        return error;
    }
}
