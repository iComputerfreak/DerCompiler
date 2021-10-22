package de.dercompiler.io.message;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;

import java.awt.*;
import java.util.Objects;

public class ANSIColorizer implements IColorizer {

    private int convertChannel(int color) {
        if (color <= 64) {
            return 0;
        }
        if (color <= 192) {
            return 1;
        }
        return 2;
    }

    private int convert(Color color) {
        int red = convertChannel(color.getRed());
        int green = convertChannel(color.getGreen());
        int blue = convertChannel(color.getBlue());

        int result = 0;
        //we have more high-intensity, than standard colors
        int compare = 0;
        if (red == 2 || green == 2 || blue == 2) {
            compare = 1;
        }
        if (red + green + blue > 3) {
            result += 8;
        }
        if (red > compare ) {
            result += 1;
        }
        if (green > compare) {
            result += 2;
        }
        if (blue > compare) {
            result += 4;
        }
        return result;
    }

    private Attribute toForegroundColor(Color color) {
        switch (convert(color)) {
            case 0: return Attribute.BLACK_TEXT();
            case 1: return Attribute.RED_TEXT();
            case 2: return Attribute.GREEN_TEXT();
            case 3: return Attribute.YELLOW_TEXT();
            case 4: return Attribute.BLUE_TEXT();
            case 5: return Attribute.MAGENTA_TEXT();
            case 6: return Attribute.CYAN_TEXT();
            case 7: return Attribute.WHITE_TEXT();

            case 8: return Attribute.BRIGHT_BLACK_TEXT();
            case 9: return Attribute.BRIGHT_RED_TEXT();
            case 10: return Attribute.BRIGHT_GREEN_TEXT();
            case 11: return Attribute.BRIGHT_YELLOW_TEXT();
            case 12: return Attribute.BRIGHT_BLUE_TEXT();
            case 13: return Attribute.BRIGHT_MAGENTA_TEXT();
            case 14: return Attribute.BRIGHT_CYAN_TEXT();
            case 15: return Attribute.BRIGHT_WHITE_TEXT();
        }
        return null;
    }

    private Attribute toBackgroundColor(Color color) {
        switch (convert(color)) {
            case 0: return Attribute.BLACK_BACK();
            case 1: return Attribute.RED_BACK();
            case 2: return Attribute.GREEN_BACK();
            case 3: return Attribute.YELLOW_BACK();
            case 4: return Attribute.BLUE_BACK();
            case 5: return Attribute.MAGENTA_BACK();
            case 6: return Attribute.CYAN_BACK();
            case 7: return Attribute.WHITE_BACK();

            case 8: return Attribute.BRIGHT_BLACK_BACK();
            case 9: return Attribute.BRIGHT_RED_BACK();
            case 10: return Attribute.BRIGHT_GREEN_BACK();
            case 11: return Attribute.BRIGHT_YELLOW_BACK();
            case 12: return Attribute.BRIGHT_BLUE_BACK();
            case 13: return Attribute.BRIGHT_MAGENTA_BACK();
            case 14: return Attribute.BRIGHT_CYAN_BACK();
            case 15: return Attribute.BRIGHT_WHITE_BACK();
        }
        return null;
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
            fgc = toForegroundColor(foreground);
        }
        if (!Objects.isNull(background)) {
            bgc = toBackgroundColor(background);
        }
        return Ansi.colorize(message, ColorizationHelper.removeInvalid(fgc, bgc));
    }
}
