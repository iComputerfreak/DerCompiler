package de.dercompiler.util;

public class ErrorStatus {

    private static final int NO_ERROR = 0;
    private static int errorCode = 0;
    private static boolean hasError = false;

    public static void reportError(int error) {
        if (!hasError) {
            hasError = true;
            errorCode = error;
        }
    }

    public static boolean hasError() {
        return hasError;
    }

    public static void exitProgramIfError() {
        if (hasError) {
            System.exit(errorCode);
        }
    }

    public static void exitProgram() {
        exitProgramIfError();
        System.exit(NO_ERROR);
    }
}
