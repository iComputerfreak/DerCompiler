package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.message.MessageOrigin;
import org.apache.commons.cli.*;
import static de.dercompiler.io.CommandLineStrings.*;

/**
 * This is the central location for defining commandline arguments, and parse them using the commons-cli api
 */
public class CommandLineBuilder {

    /**
     * Adds the commandline-options to the options variable
     *
     * @param options The options variable to store all used commandline arguments
     */
    private static void buildOptions(Options options) {
        // general
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_ECHO, false, DESCRIPTION_ECHO)); // --echo
        options.addOption(new Option(COMMAND_HELP_SHORT, COMMAND_HELP, false, DESCRIPTION_HELP)); // --help, -h
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_LEX_TEST, false, DESCRIPTION_LEX_TEST)); // --lextest
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PARSE_TEST, false, DESCRIPTION_PARSE_TEST)); // --parsetest
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_AST, false, DESCRIPTION_PRINT_AST)); // --parsetest
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_CHECK, false, DESCRIPTION_CHECK)); // --check
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_COMPILE_FIRM, false, DESCRIPTION_COMPILE_FIRM)); //--compile-firm

        // options
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_WORKING_DIR, true, DESCRIPTION_WORKING_DIR)); // --working-directory
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_PRINT_POSITION, false, DESCRIPTION_PRINT_POSITION));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_PARSE_METHOD, false, DESCRIPTION_PARSE_METHOD));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_PARSE_STATEMENT, false, DESCRIPTION_PARSE_STATEMENT));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_PARSE_EXPRESSION, false, DESCRIPTION_PARSE_EXPRESSION));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_PRETTY_PRINT, false, DESCRIPTION_PRETTY_PRINT));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_PRINT_PIPELINE, false, DESCRIPTION_PRINT_PIPELINE));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, OPTION_TIME_EXECUTION, false, DESCRIPTION_TIME_EXECUTION));

        // colors
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_NO_COLOR, false, DESCRIPTION_PRINT_NO_COLOR)); // --no-color
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_ANSI_COLOR, false, DESCRIPTION_PRINT_ANSI_COLOR)); // --ansi-color
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_8BIT_COLOR, false, DESCRIPTION_PRINT_8BIT_COLOR)); // --8bit-color
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_TRUE_COLOR, false, DESCRIPTION_PRINT_TRUE_COLOR)); // --true-color

        // warnings
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_STACKTRACE, false, DESCRIPTION_PRINT_STACKTRACE)); // --print-stacktrace
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_WARNING_AS_ERRORS, false, DESCRIPTION_WARNINGS_AS_ERRORS)); // --warnings-as-errors

        // TODO add options for specific parts of the compiler
        // options
        options.addOption(new Option(OPTION_LEX_STRING_SHORT, OPTION_LEX_STRING, true, DESCRIPTION_LEX_STRING)); // --lex-string, -s

    }

    /**
     * Parses the commandline arguments based on the arguments handed over
     *
     * @param args The arguments typically passed to the main function
     * @return The parsed arguments
     */
    public CommandLineOptions parseArguments(String[] args) {
        Options options = new Options();
        buildOptions(options);

        CommandLineParser clp = new DefaultParser(false);
        CommandLineOptions clo = null;
        try {
            clo = new CommandLineOptions(clp.parse(options, args, false));
        } catch (ParseException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL)
                    .printErrorAndExit(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, "Some arguments caused the CommandlineParser to stop working!", e);
        }
        // Can't return 0, when error, we exit the program
        return clo;
    }

    /**
     * Prints the help information to System.out to display how to use the Compiler
     *
     * @param compiler The name of the compiler
     */
    public static void printHelp(String compiler) {
        Options options = new Options();
        buildOptions(options);

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(compiler, options);
    }
}
