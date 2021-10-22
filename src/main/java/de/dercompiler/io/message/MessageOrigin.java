package de.dercompiler.io.message;

import java.awt.*;

public enum MessageOrigin {
    GENERAL("COM",new Color(0,0,0), new Color(0,0,0), new Color(0,0,0), new Color(0,0,0)),
    LEXER("LEX",new Color(0, 0,0), new Color(0, 0, 0), new Color(0,0,0), new Color(0,0,0)),
    PARSER("PAR",new Color(0, 0,0), new Color(0, 0, 0), new Color(0,0,0), new Color(0,0,0))

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
