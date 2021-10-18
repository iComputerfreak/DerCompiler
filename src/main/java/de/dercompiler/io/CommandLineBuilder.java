package de.dercompiler.io;

import org.apache.commons.cli.*;

public class CommandLineBuilder {

    private static void buildOptions(Options options) {
        //general
        options.addOption(new Option(CommandLineStrings.COMMAND_NO_SHORT_OPTION, CommandLineStrings.COMMAND_ECHO, false, CommandLineStrings.DESCRIPTION_ECHO));
        options.addOption(new Option(CommandLineStrings.COMMAND_HELP_SHORT, CommandLineStrings.COMMAND_HELP, false, CommandLineStrings.DESCRIPTION_HELP));
        options.addOption(new Option(CommandLineStrings.COMMAND_NO_SHORT_OPTION, CommandLineStrings.COMMAND_RELATIVE_PATH, true, CommandLineStrings.DESCRIPTION_RELATIVE_PATH));

        //TODO add options for specific parts of the compiler


    }

    public void parseArguments(String[] args) {
        Options options = new Options();
        buildOptions(options);

        CommandLineParser clp = new DefaultParser();
        try {
            CommandLine cmd = clp.parse(options, args, false);
            CommandLineOptions.getInstance().setCommandLine(cmd);
        } catch (ParseException e) {
            e.printStackTrace();
            //TODO add good error handling
            System.exit(-1);
        }
    }

    public static void printHelp(String compiler) {
        Options options = new Options();
        buildOptions(options);

        HelpFormatter formater = new HelpFormatter();
        formater.printHelp(compiler, options);
    }
}
