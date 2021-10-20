package de.dercompiler.io;

import org.apache.commons.cli.CommandLine;

import java.util.List;
import static de.dercompiler.io.CommandLineStrings.*;

public class CommandLineOptions {

    private CommandLine cmd;
    public CommandLineOptions(CommandLine cmd) { this.cmd = cmd; }


    public String root() {
        if (cmd.hasOption(COMMAND_WORKING_DIR)) {
            return cmd.getOptionValue(COMMAND_WORKING_DIR);
        }
        return null;
    }

    public boolean echo() {
        return cmd.hasOption(COMMAND_ECHO);
    }

    public boolean help() { return cmd.hasOption(COMMAND_HELP); }

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
