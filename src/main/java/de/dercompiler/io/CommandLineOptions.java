package de.dercompiler.io;

import org.apache.commons.cli.CommandLine;

import java.util.List;

public class CommandLineOptions {

    private static CommandLineOptions clo = null;

    private CommandLine cmd;

    private CommandLineOptions() { }


    public static CommandLineOptions getInstance() {
        if (clo == null) {
            clo = new CommandLineOptions();
        }
        return clo;
    }

    public void setCommandLine(CommandLine cmd) {
        this.cmd = cmd;
    }

    public String relativeRoot() {
        if (cmd.hasOption(CommandLineStrings.COMMAND_RELATIVE_PATH)) {
            return cmd.getOptionValue(CommandLineStrings.COMMAND_RELATIVE_PATH);
        }
        return null;
    }

    public boolean echo() {
        return cmd.hasOption(CommandLineStrings.COMMAND_ECHO);
    }

    public boolean help() { return cmd.hasOption(CommandLineStrings.COMMAND_HELP); }

    public String[] unparsedArguments() {
        return cmd.getArgs();
    }

    public List<String> unparsedArgumentsList() {
        return cmd.getArgList();
    }

    public long getNumberOfUnparsedArguments() {
        return cmd.getArgList().size();
    }


}
