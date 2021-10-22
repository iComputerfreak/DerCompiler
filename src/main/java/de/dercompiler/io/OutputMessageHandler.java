package de.dercompiler.io;

import de.dercompiler.io.message.*;

import java.awt.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

public final class OutputMessageHandler {

    private static final String INFO = "info";
    
    private static final String INFO_MESSAGE = "info: ";
    private static final String WARNING_MESSAGE = "warning: ";
    private static final String ERROR_MESSAGE = "error: ";
    
    // X is the origin of the output, Y the reason  "[XXXYYYY] "
    private static final String SKIP_MESSAGE_HEAD = "          ";

    private static IColorizer globalColorizer = new NoColorColorizer();
    private static boolean globalPrintStackTrace = false;
    private static boolean globalWarningAsError = false;
    private static final int PREFIX_MULTIPLIER = 10000;

    private String ident;
    private int idPrefix;

    private Color textColor;
    private Color infoColor;
    private Color warningColor;
    private Color errorColor;

    private PrintStream stream;
    private IColorizer colorizer;

    private boolean printStackTrace;

    public OutputMessageHandler(MessageOrigin origin, PrintStream stream) {
        ident = origin.getIdentifier();
        textColor = origin.getTextColor();
        infoColor = origin.getInfoColor();
        warningColor = origin.getWarningColor();
        errorColor = origin.getErrorColor();

        colorizer = globalColorizer;
        this.stream = stream;

        printStackTrace = globalPrintStackTrace;
        idPrefix = origin.getId() * PREFIX_MULTIPLIER;
    }

    private String formatId(int id) {
        return String.format("%04d", id);
    }

    private void formatMessage(String messageHead, Color messageHeadColor, String message, Color messageColor) {
        stream.print("[" + colorizer.colorize(messageHeadColor, messageHead) + "] ");
        stream.println(colorizer.colorize(messageColor, message.replace("\n", "\n" + SKIP_MESSAGE_HEAD)));
    }

    private void formatMessage(String messageHead, Color messageHeadColor, String message, Color messageColor, Exception e) {
        formatMessage(messageHead, messageHeadColor, message, messageColor, e, messageColor);
    }

    private void formatMessage(String messageHead, Color messageHeadColor, String message, Color messageColor, Exception e, Color errorColor) {
        formatMessage(messageHead, messageHeadColor, message + "\nexception-message: " + e.getMessage(), messageColor);

        if (printStackTrace) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            stream.println(colorizer.colorize(errorColor, sw.toString().replace("\n", "\n" + SKIP_MESSAGE_HEAD)));
        } else {
            stream.println(colorizer.colorize(infoColor, "use option --print-stacktrace, to see the stacktrace of the Exception!"));
        }
    }

    public void printInfo(String infoMessage) {
        formatMessage(ident + INFO, infoColor, INFO_MESSAGE + infoMessage, textColor);
    }

    public void printWarning(IWarningIds id, String warningMessage) {
        if (globalWarningAsError) {
            printError(id, warningMessage);
        } else {
            formatMessage(ident + formatId(id.getId()), warningColor, WARNING_MESSAGE + warningMessage, warningColor);
        }
    }

    public void printWarningWithException(IWarningIds id, String warningMessage, Exception e) {
        if (globalWarningAsError) {
            printError(id, warningMessage, e);
        } else {
            formatMessage(ident + formatId(id.getId()), warningColor, WARNING_MESSAGE + warningMessage, warningColor, e, errorColor);
        }
    }

    public void printError(IErrorIds id, String errorMessage) {
        printErrorAndContinue(id, errorMessage);
        System.exit(-idPrefix - id.getId());
    }

    public void printErrorAndContinue(IErrorIds id, String errorMessage) {
        formatMessage(ident + formatId(id.getId()), errorColor, ERROR_MESSAGE + errorMessage, errorColor);
    }

    public void printError(IErrorIds id, String errorMessage, Exception e) {
        printErrorAndContinue(id, errorMessage, e);
        System.exit(-idPrefix - id.getId());
    }

    public void printErrorAndContinue(IErrorIds id, String errorMessage, Exception e) {
        formatMessage( ident + formatId(id.getId()), errorColor, ERROR_MESSAGE + errorMessage, errorColor, e);
    }

    public void noColorOutput() {
        colorizer = new NoColorColorizer();
    }
    
    public static void useNoColors() {
        globalColorizer = new NoColorColorizer();
    }

    public static void useANSIColors() {
        globalColorizer = new ANSIColorizer();
    }

    public static void use8BitColors() {
        globalColorizer = new Colorizer8Bit();
    }

    public static void use24BitColors() {
        globalColorizer = new Colorizer24Bit();
    }

    public static void setErrorAsWarning(boolean active) {
        globalWarningAsError = active;
    }

    public static void setPrintStackTrace(boolean print) {
        globalPrintStackTrace = print;
    }
}
