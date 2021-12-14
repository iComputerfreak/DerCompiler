package de.dercompiler.io;

/**
 * Defines all strings used in the Commandline, grouped by their use:
 *
 *  1. general unique strings
 *  2. commands
 *  3. descriptions
 */
public class CommandLineStrings {

    // General
    public static final String COMMANDLINE_USING_STRING = "";

    // Commands
    public static final String COMMAND_NO_SHORT_OPTION = null;
    public static final String COMMAND_ECHO = "echo";
    public static final String COMMAND_HELP_SHORT = "h";
    public static final String COMMAND_HELP = "help";
    public static final String COMMAND_LEX_TEST = "lextest";
    public static final String COMMAND_PARSE_TEST = "parsetest";
    public static final String COMMAND_PRINT_AST = "print-ast";
    public static final String COMMAND_CHECK = "check";
    public static final String COMMAND_COMPILE_FIRM = "compile-firm";

    public static final String COMMAND_WORKING_DIR = "working-directory";

    public static final String COMMAND_PRINT_STACKTRACE = "print-stacktrace";
    public static final String COMMAND_WARNING_AS_ERRORS = "warnings-as-errors";

    public static final String COMMAND_PRINT_NO_COLOR = "no-color";
    public static final String COMMAND_PRINT_ANSI_COLOR = "ansi-color";
    public static final String COMMAND_PRINT_8BIT_COLOR = "8bit-color";
    public static final String COMMAND_PRINT_TRUE_COLOR = "true-color";

    // Options
    public static final String OPTION_LEX_STRING = "lex-string";
    public static final String OPTION_LEX_STRING_SHORT = "s";
    public static final String OPTION_PRINT_POSITION = "print-position";

    public static final String OPTION_PARSE_METHOD = "method";
    public static final String OPTION_PARSE_STATEMENT = "statement";
    public static final String OPTION_PARSE_EXPRESSION = "expression";

    public static final String OPTION_NO_MAIN = "no-main";

    public static final String OPTION_PRETTY_PRINT = "pretty-print";
    public static final String OPTION_PRINT_PIPELINE = "print-pipeline";
    public static final String OPTION_TIME_EXECUTION = "time";
    public static final String OPTION_NO_INFO = "no-info";

    public static final String OPTION_TARGET = "target";

    // Descriptions
    public static final String DESCRIPTION_ECHO = "Prints the content of the file";
    public static final String DESCRIPTION_HELP = "Print all options";
    public static final String DESCRIPTION_LEX_TEST = "Prints the sequence of tokens in the input";
    public static final String DESCRIPTION_PARSE_TEST = "Prints an error if the input could not be parsed correctly";
    public static final String DESCRIPTION_PRINT_AST = "Pretty-prints the generated AST to the console.";
    public static final String DESCRIPTION_CHECK = "Checks the code for semantics.";
    public static final String DESCRIPTION_COMPILE_FIRM = "compile with firm backend";

    public static final String DESCRIPTION_WORKING_DIR = "Overrides the working directory of the compiler";

    public static final String DESCRIPTION_PRINT_STACKTRACE = "Prints the stacktrace of the error or warning if available";
    public static final String DESCRIPTION_WARNINGS_AS_ERRORS = "All warnings are treated as errors";

    public static final String DESCRIPTION_PARSE_METHOD = "(--parseTest) Starts to parse at Method level instead of Program level";
    public static final String DESCRIPTION_PARSE_STATEMENT = "(--parseTest) Starts to parse at Statement level instead of Program level";
    public static final String DESCRIPTION_PARSE_EXPRESSION = "(--parseTest) Starts to parse at Expression level instead of Program level";

    public static final String DESCRIPTION_PRINT_NO_COLOR = "No color output";
    public static final String DESCRIPTION_PRINT_ANSI_COLOR = "Classic 16-bit color output";
    public static final String DESCRIPTION_PRINT_8BIT_COLOR = "8-bit color output";
    public static final String DESCRIPTION_PRINT_TRUE_COLOR = "True color output";

    public static final String DESCRIPTION_LEX_STRING = "(--lextest) Lexes a String instead of a file";
    public static final String DESCRIPTION_PRINT_POSITION = "(--lextest) Prints the positions of the token occurrences";

    public static final String DESCRIPTION_NO_MAIN = "compile a program with or without a main function, this may not generate a valid program.";

    public static final String DESCRIPTION_PRETTY_PRINT = "(--parsetest) Pretty prints the constructed AST as code.";
    public static final String DESCRIPTION_PRINT_PIPELINE = "Prints the pipeline whenever one is used.";
    public static final String DESCRIPTION_TIME_EXECUTION = "Prints the execution-time at the end of execution";

    public static final String DESCRIPTION_TARGET = "The name of the target to compile";
    public static final String DESCRIPTION_NO_INFO = "Deactivates info print";

    //compiler assembler and linker
    public static final String OPTION_CLANG         = "clang";
    public static final String OPTION_LLD           = "clang-lld";
    public static final String DESCRIPTION_CLANG    = "set clang as compiler use --lld to set linker";
    public static final String DESCRIPTION_LLD      = "set lld as linker use --clang to set compiler";

    public static final String OPTION_GCC           = "gcc";
    public static final String OPTION_LD            = "gcc-ld";
    public static final String DESCRIPTION_GCC      = "set gcc as compiler use --ld to set linker";
    public static final String DESCRIPTION_LD       = "set ld as linker use --gcc to set compiler";

    public static final String OPTION_CL           = "cl";
    public static final String DESCRIPTION_CL      = "sets the microsoft cl as compiler and linker, only Windows support";

    public static final String OPTION_NASM         = "nasm";
    public static final String DESCRIPTION_NASM    = "sets nasm as assembler";

}
