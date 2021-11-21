package de.dercompiler.util;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public class ErrorStatus {

    private static final int NO_ERROR = 0;
    private static int errorCode = 0;
    private static boolean hasError = false;
    private static long start = System.currentTimeMillis();
    private static boolean printTime = false;

    public static void reportError(int error) {
        if (!hasError) {
            hasError = true;
            errorCode = error;
        }
    }

    public static boolean hasError() {
        return hasError;
    }

    public static void setPrintTIme() { printTime = true; }

    public static void exit(int returncode) {
        if (printTime) {
            StringBuilder sb = new StringBuilder();
            sb.append("Execution time: ");
            sb.append(System.currentTimeMillis() - start);
            sb.append("ms.");
            new OutputMessageHandler(MessageOrigin.GENERAL).printInfo(sb.toString());
        }
        System.exit(returncode);
    }

    public static void exitProgramIfError() {
        if (hasError) {
            exit(errorCode);
        }
    }

    public static void exitProgram() {
        exitProgramIfError();
        exit(NO_ERROR);
    }
}
