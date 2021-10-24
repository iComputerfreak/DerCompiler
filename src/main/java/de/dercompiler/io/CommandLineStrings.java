package de.dercompiler.io;

/**
 * defines all strings used in the Commandline, there are grouped bye there use:
 *
 *  1. general unique strings
 *  2. command
 *  3. description
 */
public class CommandLineStrings {

    //general
    public static final String COMMANDLINE_USING_STRING = "";

    //commands
    public static final String COMMAND_NO_SHORT_OPTION = null;
    public static final String COMMAND_ECHO = "echo";
    public static final String COMMAND_HELP = "help";
    public static final String COMMAND_HELP_SHORT = "h";
    public static final String COMMAND_WORKING_DIR = "working-directory";

    public static final String COMMAND_PRINT_STACKTRACE = "print-stacktrace";
    public static final String COMMAND_WARNING_AS_ERRORS = "warnings-as-errors";

    public static final String COMMAND_PRINT_NO_COLOR = "no-color";
    public static final String COMMAND_PRINT_ANSI_COLOR = "ansi-color";
    public static final String COMMAND_PRINT_8BIT_COLOR = "8bit-color";
    public static final String COMMAND_PRINT_TRUE_COLOR = "true-color";


    //descriptions
    public static final String DESCRIPTION_ECHO = "Prints the content of the file";
    public static final String DESCRIPTION_HELP = "Print all options";
    public static final String DESCRIPTION_WORKING_DIR = "Overrides the working directory of the compiler";

    public static final String DESCRIPTION_PRINT_STACKTRACE = "Prints the Stacktrace of the error or warning if available";
    public static final String DESCRIPTION_WARNINGS_AS_ERRORS = "All warnings are treated as errors";

    public static final String DESCRIPTION_PRINT_NO_COLOR = "no color output";
    public static final String DESCRIPTION_PRINT_ANSI_COLOR = "classic 16 color output";
    public static final String DESCRIPTION_PRINT_8BIT_COLOR = "8-bit color output";
    public static final String DESCRIPTION_PRINT_TRUE_COLOR = "true color output";
}
