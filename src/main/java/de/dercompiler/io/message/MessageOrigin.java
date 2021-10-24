package de.dercompiler.io.message;

import java.awt.*;

/**
 * The MessageOrigin describes the origin of the message and depending on this information generate different error and warning-codes.
 * Part of the MessageOrigin, is the color-flair of the messages
 */
public enum MessageOrigin {
    GENERAL("COM", null, new Color(0x20,0x63,0x9B), new Color(0xF6,0xD5,0x5C), new Color(0xED,0x55,0x3B)),
    LEXER("LEX", null, new Color(0, 0, 0), new Color(0,0,0), new Color(0,0,0)),
    PARSER("PAR", null, new Color(0, 0, 0), new Color(0,0,0), new Color(0,0,0))

    //add more origins
    ;

    private String identifier;

    private Color text;
    private Color info;
    private Color warning;
    private Color error;

    MessageOrigin(String identifier, Color text, Color info, Color warning, Color error) {
        this.identifier = identifier;

        this.text = text;
        this.info = info;
        this.warning = warning;
        this.error = error;
    }

    public int getId() {
        return this.ordinal();
    }

    public String getIdentifier() {
        return identifier;
    }

    public Color getTextColor() {
        return text;
    }

    public Color getInfoColor() {
        return info;
    }

    public Color getWarningColor() {
        return warning;
    }

    public Color getErrorColor() {
        return error;
    }
}
