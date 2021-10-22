package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.message.MessageOrigin;
import org.apache.commons.cli.*;
import static de.dercompiler.io.CommandLineStrings.*;

public class CommandLineBuilder {

    private static void buildOptions(Options options) {
        //general
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION,COMMAND_ECHO, false, DESCRIPTION_ECHO));
        options.addOption(new Option(COMMAND_HELP_SHORT, COMMAND_HELP, false, DESCRIPTION_HELP));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_WORKING_DIR, true, DESCRIPTION_WORKING_DIR));

        //colors
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_NO_COLOR, false, DESCRIPTION_PRINT_NO_COLOR));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_ANSI_COLOR, false, DESCRIPTION_PRINT_ANSI_COLOR));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_8BIT_COLOR, false, DESCRIPTION_PRINT_8BIT_COLOR));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_TRUE_COLOR, false, DESCRIPTION_PRINT_TRUE_COLOR));

        //warnings
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_PRINT_STACKTRACE, false, DESCRIPTION_PRINT_STACKTRACE));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_WARNING_AS_ERRORS, false, DESCRIPTION_WARNINGS_AS_ERRORS));

        //TODO add options for specific parts of the compiler


    }

    public CommandLineOptions parseArguments(String[] args) {
        Options options = new Options();
        buildOptions(options);

        CommandLineParser clp = new DefaultParser();
        CommandLineOptions clo = null;
        try {
            clo = new CommandLineOptions(clp.parse(options, args, false));
        } catch (ParseException e) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err)
                    .printError(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, "Some arguments caused the CommandlineParser to stop working!", e);
        }
        //can't return 0, when error, we exit the program
        return clo;
    }

    public static void printHelp(String compiler) {
        Options options = new Options();
        buildOptions(options);

        HelpFormatter formatter = new HelpFormatter();
        formatter.printHelp(compiler, options);
    }
}
