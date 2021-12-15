package de.dercompiler.io;

import de.dercompiler.io.message.*;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.util.ErrorStatus;

import java.awt.*;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

/**
 * The OutputMessageHandler provides a generalized interface to handle errors, warning and info messages, if set the
 * output gets automatically colorized in the supported color-mode.
 */
public final class OutputMessageHandler {

    private static final String INFO = " info";
    private static final String INTERNAL = "internal";

    private static final String INFO_MESSAGE = "info: ";
    private static final String WARNING_MESSAGE = "warning: ";
    private static final String ERROR_MESSAGE = "error: ";

    private static final int ERROR_CODE = 69;
    
    // X is the origin of the output, Y the reason  "[XXXYYYYY] "
    private static final String SKIP_MESSAGE_HEAD = "           ";

    private static IColorizer globalColorizer = new NoColorColorizer();
    private static boolean globalPrintStackTrace = false;
    private static boolean globalWarningAsError = false;
    private static final int PREFIX_MULTIPLIER = 100000;

    private static final int CALLER_STACKTRACE = 1;

    private final String ident;
    private final int idPrefix;

    private final MessageOrigin origin;
    private final Color textColor;
    private final Color infoColor;
    private final Color warningColor;
    private final Color errorColor;

    private IColorizer colorizer;

    private final boolean printStackTrace;

    private static boolean debug_mode = false;
    private static boolean debug_print = true;
    private static boolean print_info = true;
    private static final List<DebugEvent> debugEvents = new LinkedList<>();

    /**
     * Creates a new OutputMessageHandler with the given origin and stream
     *
     * @param origin The origin of the messages
     */
    public OutputMessageHandler(MessageOrigin origin) {
        this.origin = origin;
        ident = origin.getIdentifier();
        textColor = origin.getTextColor();
        infoColor = origin.getInfoColor();
        warningColor = origin.getWarningColor();
        errorColor = origin.getErrorColor();

        colorizer = globalColorizer;

        printStackTrace = globalPrintStackTrace;
        idPrefix = origin.getId() * PREFIX_MULTIPLIER;
    }

    /**
     * Formats the given ID as a 5-digit integer number
     * @param id The ID to format
     * @return The zero-padded 5-digit number
     */
    private String formatId(int id) {
        return String.format("%05d", id);
    }

    /**
     * Formats the given message
     * @param messageHead The prefix of the message
     * @param messageHeadColor The color of the message prefix
     * @param message The message itself
     * @param messageColor The color of the message
     */
    private void formatMessage(PrintStream stream, String messageHead, Color messageHeadColor, String message, Color messageColor) {
        if (debug_print) {
            stream.print("[" + colorizer.colorize(messageHeadColor, messageHead) + "] ");
            stream.println(colorizer.colorize(messageColor, message.replace("\n", "\n" + SKIP_MESSAGE_HEAD)));
        }
    }

    /**
     * Formats the given message with an exception
     * @param messageHead The prefix of the message
     * @param messageHeadColor The color of the message prefix
     * @param message The message itself
     * @param messageColor The color of the message
     * @param e The exception
     */
    private void formatMessage(PrintStream stream, String messageHead, Color messageHeadColor, String message, Color messageColor, Exception e) {
        formatMessage(stream, messageHead, messageHeadColor, message, messageColor, e, messageColor);
    }
    
    /**
     * Formats the given message with an exception
     * @param messageHead The prefix of the message
     * @param messageHeadColor The color of the message prefix
     * @param message The message itself
     * @param messageColor The color of the message
     * @param e The exception
     * @param errorColor The color of the exception
     */
    private void formatMessage(PrintStream stream, String messageHead, Color messageHeadColor, String message, Color messageColor, Exception e, Color errorColor) {
        formatMessage(stream, messageHead, messageHeadColor, message + "\n" + "exception-message: " + e.getMessage(), messageColor);

        if (printStackTrace) {
            StringWriter sw = new StringWriter();
            e.printStackTrace(new PrintWriter(sw));
            stream.println(colorizer.colorize(errorColor, sw.toString().replace("\n", "\n" + SKIP_MESSAGE_HEAD)));
        } else {
            stream.println(colorizer.colorize(infoColor, "use option --print-stacktrace, to see the stacktrace of the Exception!"));
        }
    }

    /**
     * Prints an info message
     *
     * @param infoMessage The info message to print
     */
    public void printInfo(String infoMessage) {
        if (print_info) {
            formatMessage(System.out, ident + INFO, infoColor, INFO_MESSAGE + infoMessage, textColor);
        }
    }

    /**
     * Prints a warning message
     *
     * @param id The id of the warning
     * @param warningMessage The warning-message to print
     */
    public void printWarning(IWarningIds id, String warningMessage) {
        if (globalWarningAsError) {
            printErrorAndExit(id, warningMessage);
        } else {
            formatMessage(System.err, ident + formatId(id.getId()), warningColor, WARNING_MESSAGE + warningMessage, warningColor);
        }
    }

    /**
     * Prints a warning message
     *
     * @param id The id of the warning
     * @param warningMessage The warning message to print
     * @param e The exception that may get printed, depending on the global state
     */
    public void printWarningWithException(IWarningIds id, String warningMessage, Exception e) {
        if (globalWarningAsError) {
            printErrorAndExit(id, warningMessage, e);
        } else {
            formatMessage(System.err, ident + formatId(id.getId()), warningColor, WARNING_MESSAGE + warningMessage, warningColor, e, errorColor);
        }
    }

    /**
     * Prints an error message and exits the program with the generated exit code
     *
     * @param id The id of the warning
     * @param errorMessage The error message to print
     */
    public void printErrorAndExit(IErrorIds id, String errorMessage) {
        printErrorAndContinue(id, errorMessage);
        if (debug_mode) {
            debugEvents.add(new DebugEvent(origin, id, errorMessage));
        } else {
            ErrorStatus.exit(ERROR_CODE);
        }
    }

    /**
     * Prints an error message and continues, only call this function, if we know we will print an error message with printError later.
     *
     * @param id The id of the warning
     * @param errorMessage The error message to print
     */
    public void printErrorAndContinue(IErrorIds id, String errorMessage) {
        formatMessage(System.err, ident + formatId(id.getId()), errorColor, ERROR_MESSAGE + errorMessage, errorColor);
        ErrorStatus.reportError(ERROR_CODE);
    }

    /**
     * Prints an error message and exits the program with the generated exit code
     *
     * @param id The id of the warning
     * @param errorMessage The error-message to print
     * @param e The exception that may get printed, depending on the global state
     */
    public void printErrorAndExit(IErrorIds id, String errorMessage, Exception e) {
        printErrorAndContinue(id, errorMessage, e);
        if (debug_mode) {
            debugEvents.add(new DebugEvent(origin, id, errorMessage));
        } else {
            ErrorStatus.exit(ERROR_CODE);
        }
    }

    /**
     * Prints an error message and continues, only call this function, if we know we will print an error message with printError later.
     *
     * @param id The id of the warning
     * @param errorMessage The error-message to print
     * @param e The exception that may get printed, depending on the global state
     */
    public void printErrorAndContinue(IErrorIds id, String errorMessage, Exception e) {
        formatMessage( System.err, ident + formatId(id.getId()), errorColor, ERROR_MESSAGE + errorMessage, errorColor, e);
        ErrorStatus.reportError(ERROR_CODE);
    }

    public void printParserError(IErrorIds id, String errorMessage, Lexer lexer, SourcePosition position) {
        printErrorAndContinue(id, errorMessage + "\n" + lexer.printSourceText(position));
        if (debug_mode) {
            debugEvents.add(new DebugEvent(origin, id, errorMessage + "\n" + lexer.printSourceText(position)));
        } else {
            ErrorStatus.reportError(ERROR_CODE);
        }
    }

    private void internalError(String message, StackTraceElement element) {
        formatMessage(System.err, INTERNAL, errorColor, "Internal error at " + element.getClassName() + "."
                + element.getMethodName() + "() in line " + element.getLineNumber() + (Objects.isNull(message) ? "." : ":\n" + message), errorColor);
        ErrorStatus.exit(-1);
    }

    private void internalError(String message, StackTraceElement element, Exception e) {
        formatMessage(System.err, INTERNAL, errorColor, "Internal error at " + element.getClassName() + "."
                + element.getMethodName() + "() in line " + element.getLineNumber() + (Objects.isNull(message) ? "." : ":\n" + message), errorColor, e);
        ErrorStatus.exit(-1);
    }

    public void printPlain(String string) {
        System.out.println(string);
    }

    public void printPlane() {
        String plane = """
                                                                  .____   __ _
     __o__   _______ _ _  _                                     /     /
     \\    ~\\                                                  /      /
       \\     '\\                                         ..../      .'
        . ' ' . ~\\                                      ' /       /
       .  _    .  ~ \\  .+~\\~ ~ ' ' " " ' ' ~ - - - - - -''_      /
      .  <#  .  - - -/' . ' \\  __                          '~ - \\
       .. -           ~-.._ / |__|  ( )  ( )  ( )  0  o    _ _    ~ .
     .-'                                               .- ~    '-.    -.
    <                      . ~ ' ' .             . - ~             ~ -.__~_. _ _
      ~- .       N121PP  .          . . . . ,- ~
            ' ~ - - - - =.   <#>    .         \\.._
                        .     ~      ____ _ .. ..  .- .
                         .         '        ~ -.        ~ -.
                           ' . . '               ~ - .       ~-.
                                                       ~ - .      ~ .
                                                              ~ -...0..~. ____
""";
        // by Dick Williams, rjw1@tyrell.net
        System.out.println(plane);
    }

    public void debugPrint(String string) {
        if (print_info) {
            System.out.println("debug: " + string);
        }
    }

    /**
     * Prints an internal error, with classname, methode, and line
     */
    public void internalError() {
        //https://stackoverflow.com/questions/7483421/how-to-get-source-file-name-line-number-from-a-java-lang-class-object
        //we use this to get the classname and the line-count where the error gets printed
        Exception e = new Exception();
        internalError(null, e.getStackTrace()[CALLER_STACKTRACE]);
    }

    /**
     * Prints an internal error with message, with classname, methode, and line
     *
     * @param errorMessage error-message to print
     */
    public void internalError(String errorMessage) {
        //https://stackoverflow.com/questions/7483421/how-to-get-source-file-name-line-number-from-a-java-lang-class-object
        //we use this to get the classname and the line-count where the error gets printed
        Exception e = new Exception();
        internalError(errorMessage, e.getStackTrace()[CALLER_STACKTRACE]);
    }

    /**
     * Prints an internal error with message, with classname, methode, and line
     *
     * @param errorMessage error-message to print
     */
    public void internalError(String errorMessage, Exception exc) {
        //https://stackoverflow.com/questions/7483421/how-to-get-source-file-name-line-number-from-a-java-lang-class-object
        //we use this to get the classname and the line-count where the error gets printed
        Exception e = new Exception();
        internalError(errorMessage, e.getStackTrace()[CALLER_STACKTRACE], exc);
    }

    /**
     * Sets no color output for the current object
     */
    public void noColorOutput() {
        colorizer = new NoColorColorizer();
    }

    /**
     * Sets the global state, all now created Objects will not print colors to their stream
     */
    public static void useNoColors() {
        globalColorizer = new NoColorColorizer();
    }

    /**
     * Sets the global state, all now created Objects will print ansi colors (4-bit) to their stream
     */
    public static void useANSIColors() {
        globalColorizer = new ANSIColorizer();
    }

    /**
     * Sets the global state, all now created objects will print 8-bit colors (6-value cube) colors to their stream
     */
    public static void use8BitColors() {
        globalColorizer = new Colorizer8Bit();
    }

    /**
     * Sets the global state, all now created objects will print true color (24-bit) colors to their stream
     */
    public static void use24BitColors() {
        globalColorizer = new Colorizer24Bit();
    }

    /**
     * Sets the global state, to handle errors as warnings
     *
     * @param active The state, if true, we will interpret errors as warnings
     *                          if false, we will treat errors and warnings normal
     */
    public static void setErrorAsWarning(boolean active) {
        globalWarningAsError = active;
    }

    /**
     * Sets the global state, so we will print the stacktrace if an exception is available
     *
     * @param print The state, if true, we will print the stacktrace if a exception is available
     *                         if false, we will print only print the exception message
     */
    public static void setPrintStackTrace(boolean print) {
        globalPrintStackTrace = print;
    }


    //debug

    public static void setDebug() {
        debug_mode = true;
    }

    public static List<DebugEvent> getEvents() {
        return debugEvents;
    }

    public static void clearDebugEvents() {
        debugEvents.clear();
    }

    public static void setTestOutput(boolean active) { if (debug_mode) debug_print = active; }

    public static void setNoInfo(boolean no_info) { print_info = !no_info; }
}
