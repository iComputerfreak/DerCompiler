package de.dercompiler.io;

import org.apache.commons.cli.*;

public class CommandLineBuilder {

    private void buildOptions(Options options) {
        //general
        options.addOption(new Option(CommandLineStrings.COMMAND_ECHO, CommandLineStrings.DESCRIPTION_ECHO));
        options.addOption(new Option(CommandLineStrings.COMMAND_RELATIVE_PATH, true, CommandLineStrings.DESCRIPTION_RELATIVE_PATH));

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
}
