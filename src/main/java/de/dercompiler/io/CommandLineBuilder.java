package de.dercompiler.io;

import org.apache.commons.cli.*;
import static de.dercompiler.io.CommandLineStrings.*;

public class CommandLineBuilder {

    private static void buildOptions(Options options) {
        //general
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION,COMMAND_ECHO, false, DESCRIPTION_ECHO));
        options.addOption(new Option(COMMAND_HELP_SHORT, COMMAND_HELP, false, DESCRIPTION_HELP));
        options.addOption(new Option(COMMAND_NO_SHORT_OPTION, COMMAND_WORKING_DIR, true, DESCRIPTION_WORKING_DIR));

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
            e.printStackTrace();
            //TODO add good error handling
            System.exit(-1);
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
