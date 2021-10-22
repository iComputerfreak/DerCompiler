package de.dercompiler.io;

import de.dercompiler.io.message.*;

import java.awt.*;
import java.io.OutputStream;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class OutputMessageHandler {

    private static final String INFO = "info";
    //                                              "[XXXYYYY] "
    private static final String SKIP_MESSAGE_HEAD = "          ";

    private static IColorizer colorizer = new NoColorColarizer();
    public static boolean warningAsError = false;

    private String ident;

    private Color textColor;
    private Color infoColor;
    private Color warningColor;
    private Color errorColor;

    private PrintStream stream;

    public OutputMessageHandler(MessageOrigin origin, PrintStream stream) {
        ident = origin.getIdentifier();
        textColor = origin.getTextColor();
        infoColor = origin.getInfoColor();
        warningColor = origin.getWarningColor();
        errorColor = origin.getErrorColor();

        this.stream = stream;
    }

    private String formatId(int id) {
        return String.format("%04d", id);
    }

    private void formatMessage(String messageHead, Color messageHeadColor, String message, Color messageColor) {
        stream.print("[" + colorizer.colorize(messageHeadColor, messageHead) + "] ");
        stream.println(        colorizer.colorize(messageColor, message.replace("\n", "\n" + SKIP_MESSAGE_HEAD)));
    }

    private void formatMessage(String messageHead, Color messageHeadColor, String message, Color messageColor, Exception e) {
        formatMessage(messageHead, messageHeadColor, message, messageColor, e, messageColor);
    }

    private void formatMessage(String messageHead, Color messageHeadColor, String message, Color messageColor, Exception e, Color errorColor) {
        formatMessage(messageHead, messageHeadColor, message, messageColor);

        StringWriter sw = new StringWriter();
        e.printStackTrace(new PrintWriter(sw));
        stream.println(colorizer.colorize(errorColor, sw.toString().replace("\n", "\n" + SKIP_MESSAGE_HEAD)));
    }

    public void printInfo(String infoMessage) {
        formatMessage(ident + INFO, infoColor, infoMessage, textColor);
    }

    public void printWarning(IWarningId id, String warningMessage) {
        if (warningAsError) {
            printError(id, warningMessage);
        } else {
            formatMessage(ident + formatId(id.getId()), warningColor, warningMessage, warningColor);
        }
    }

    public void printWarningWithException(IWarningId id, String warningMessage, Exception e) {
        if (warningAsError) {
            printError(id, warningMessage, e);
        } else {
            formatMessage(ident + formatId(id.getId()), warningColor, warningMessage, warningColor, e, errorColor);
        }
    }

    public void printError(IErrorId id, String errorMessage) {
        printErrorAndContinue(id, errorMessage);
        System.exit(-id.getId());
    }

    public void printErrorAndContinue(IErrorId id, String errorMessage) {
        formatMessage(ident + formatId(id.getId()), errorColor, errorMessage, errorColor);
    }

    public void printError(IErrorId id, String errorMessage, Exception e) {
        printErrorAndContinue(id, errorMessage, e);
        System.exit(-id.getId());
    }

    public void printErrorAndContinue(IErrorId id, String errorMessage, Exception e) {
        formatMessage( ident + formatId(id.getId()), errorColor, errorMessage, errorColor, e);
    }

    public static void useNoColors() {
        colorizer = new NoColorColarizer();
    }

    public static void useANSIColors() {
        colorizer = new ASCIColorizer();
    }

    public static void use8BitColors() {
        colorizer = new Colorizer8Bit();
    }

    public static void use24BitColors() {
        colorizer = new Colorizer24Bit();
    }

    public static void setErrorAsWarning(boolean active) {
        warningAsError = active;
    }
}
