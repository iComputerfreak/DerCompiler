package de.dercompiler.io;

import de.dercompiler.io.message.*;

import java.awt.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * The OutputMessageHandler provides a generalized interface to handle errors, warning and info messages, if set the
 * output gets automatically colorized in the supported color-mode.
 */
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

    /**
     * default Class Constructor.
     *
     * @param origin  the origin of the messages
     * @param stream  the stream to write messages to
     */
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

    /**
     * prints a info-message
     *
     * @param infoMessage the info-message to print
     */
    public void printInfo(String infoMessage) {
        formatMessage(ident + INFO, infoColor, INFO_MESSAGE + infoMessage, textColor);
    }

    /**
     * prints a warning-message
     *
     * @param id the id of the warning
     * @param warningMessage the warning-message to print
     */
    public void printWarning(IWarningIds id, String warningMessage) {
        if (globalWarningAsError) {
            printError(id, warningMessage);
        } else {
            formatMessage(ident + formatId(id.getId()), warningColor, WARNING_MESSAGE + warningMessage, warningColor);
        }
    }

    /**
     * prints a warning-message
     *
     * @param id the id of the warning
     * @param warningMessage the warning-message to print
     * @param e the exception that may get printed, depending on the global state
     */
    public void printWarningWithException(IWarningIds id, String warningMessage, Exception e) {
        if (globalWarningAsError) {
            printError(id, warningMessage, e);
        } else {
            formatMessage(ident + formatId(id.getId()), warningColor, WARNING_MESSAGE + warningMessage, warningColor, e, errorColor);
        }
    }

    /**
     * prints an error-message and exits the program with the generated exit-code
     *
     * @param id the id of the warning
     * @param errorMessage the error-message to print
     */
    public void printError(IErrorIds id, String errorMessage) {
        printErrorAndContinue(id, errorMessage);
        System.exit(-idPrefix - id.getId());
    }

    /**
     * prints an error-message and continues, only call this function, if we know we will print an error-message with printError later.
     *
     * @param id the id of the warning
     * @param errorMessage the error-message to print
     */
    public void printErrorAndContinue(IErrorIds id, String errorMessage) {
        formatMessage(ident + formatId(id.getId()), errorColor, ERROR_MESSAGE + errorMessage, errorColor);
    }

    /**
     * prints an error-message and exits the program with the generated exit-code
     *
     * @param id the id of the warning
     * @param errorMessage the error-message to print
     * @param e the exception that may get printed, depending on the global state
     */
    public void printError(IErrorIds id, String errorMessage, Exception e) {
        printErrorAndContinue(id, errorMessage, e);
        System.exit(-idPrefix - id.getId());
    }

    /**
     * prints an error-message and continues, only call this function, if we know we will print an error-message with printError later.
     *
     * @param id the id of the warning
     * @param errorMessage the error-message to print
     * @param e the exception that may get printed, depending on the global state
     */
    public void printErrorAndContinue(IErrorIds id, String errorMessage, Exception e) {
        formatMessage( ident + formatId(id.getId()), errorColor, ERROR_MESSAGE + errorMessage, errorColor, e);
    }

    /**
     * sets no color output for the current object
     */
    public void noColorOutput() {
        colorizer = new NoColorColorizer();
    }

    /**
     * sets the global state, all now created Objects will not print colors to their stream
     */
    public static void useNoColors() {
        globalColorizer = new NoColorColorizer();
    }

    /**
     * sets the global state, all now created Objects will print ansi colors(4-bit) to their stream
     */
    public static void useANSIColors() {
        globalColorizer = new ANSIColorizer();
    }

    /**
     * sets the global state, all now created objects will print 8-bit colors (6-value cube) colors to their stream
     */
    public static void use8BitColors() {
        globalColorizer = new Colorizer8Bit();
    }

    /**
     * sets the global state, all now created objects will print true color(24-bit) colors to their stream
     */
    public static void use24BitColors() {
        globalColorizer = new Colorizer24Bit();
    }

    /**
     * sets the global state, to handle errors as warnings
     *
     * @param active the state, if true, we will interpret errors as warnings
     *                          if false, we will treat errors and warnings normal
     */
    public static void setErrorAsWarning(boolean active) {
        globalWarningAsError = active;
    }

    /**
     * sets the global state, so we will print the stacktrace if an exception is available
     *
     * @param print the state, if true, we will print the stacktrace if a exception is available
     *                         if false, we will print only print the exception message
     */
    public static void setPrintStackTrace(boolean print) {
        globalPrintStackTrace = print;
    }
}
