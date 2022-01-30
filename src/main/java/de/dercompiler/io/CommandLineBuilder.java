package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.message.MessageOrigin;
import org.apache.commons.cli.*;

import static de.dercompiler.io.CommandLineStrings.*;

/**
 * This is the central location for defining commandline arguments, and parse them using the commons-cli api
 */
public class CommandLineBuilder {

    private static void createOption(Options options, String short_, String long_, boolean parameter, String description) {
        options.addOption(new Option(short_, long_, parameter, description));
    }

    /**
     * Adds the commandline-options to the options variable
     *
     * @param options The options variable to store all used commandline arguments
     */
    private static void buildOptions(Options options) {
        // general
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_ECHO, false, DESCRIPTION_ECHO); // --echo
        createOption(options, COMMAND_HELP_SHORT, COMMAND_HELP, false, DESCRIPTION_HELP); // --help, -h
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_LEX_TEST, false, DESCRIPTION_LEX_TEST); // --lextest
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PARSE_TEST, false, DESCRIPTION_PARSE_TEST); // --parsetest
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_AST, false, DESCRIPTION_PRINT_AST); // --parsetest
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_CHECK, false, DESCRIPTION_CHECK); // --check
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_COMPILE_FIRM, false, DESCRIPTION_COMPILE_FIRM); //--compile-firm
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_COMPILE, false, DESCRIPTION_COMPILE); //--compile

        // options
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_WORKING_DIR, true, DESCRIPTION_WORKING_DIR); // --working-directory
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_PRINT_POSITION, false, DESCRIPTION_PRINT_POSITION);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_PARSE_METHOD, false, DESCRIPTION_PARSE_METHOD);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_PARSE_STATEMENT, false, DESCRIPTION_PARSE_STATEMENT);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_PARSE_EXPRESSION, false, DESCRIPTION_PARSE_EXPRESSION);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_PRETTY_PRINT, false, DESCRIPTION_PRETTY_PRINT);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_PRINT_PIPELINE, false, DESCRIPTION_PRINT_PIPELINE);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_TIME_EXECUTION, false, DESCRIPTION_TIME_EXECUTION);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_NO_MAIN, false, DESCRIPTION_NO_MAIN);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_TARGET, true, DESCRIPTION_TARGET);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_NO_INFO, false, DESCRIPTION_NO_INFO);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_DUMP_GRAPH, false, DESCRIPTION_DUMP_GRAPH);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_OPTIMIZE, false, DESCRIPTION_OPTIMIZE);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_OPTIMIZATION_OFF, false, DESCRIPTION_OPTIMIZATIONS_OFF);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_OPTIMIZATION_ON, false, DESCRIPTION_OPTIMIZATIONS_ON);

        // colors
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_NO_COLOR, false, DESCRIPTION_PRINT_NO_COLOR); // --no-color
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_ANSI_COLOR, false, DESCRIPTION_PRINT_ANSI_COLOR); // --ansi-color
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_8BIT_COLOR, false, DESCRIPTION_PRINT_8BIT_COLOR); // --8bit-color
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_TRUE_COLOR, false, DESCRIPTION_PRINT_TRUE_COLOR); // --true-color

        // warnings
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_STACKTRACE, false, DESCRIPTION_PRINT_STACKTRACE); // --print-stacktrace
        createOption(options, COMMAND_NO_SHORT_OPTION, COMMAND_WARNING_AS_ERRORS, false, DESCRIPTION_WARNINGS_AS_ERRORS); // --warnings-as-errors
        
        // options
        createOption(options, OPTION_LEX_STRING_SHORT, OPTION_LEX_STRING, true, DESCRIPTION_LEX_STRING); // --lex-string, -s


        //compiler linker and assemblers
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_CL, true, DESCRIPTION_CL);

        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_CLANG, true, DESCRIPTION_CLANG);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_LLD, true, DESCRIPTION_LLD);

        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_GCC, true, DESCRIPTION_GCC);
        createOption(options, COMMAND_NO_SHORT_OPTION, OPTION_LD, true, DESCRIPTION_LD);

        createOption(options,COMMAND_NO_SHORT_OPTION, OPTION_NASM, true, DESCRIPTION_NASM);
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
