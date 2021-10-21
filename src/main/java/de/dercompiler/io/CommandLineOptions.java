package de.dercompiler.io;

import org.apache.commons.cli.CommandLine;

import java.io.File;
import java.util.List;
import static de.dercompiler.io.CommandLineStrings.*;

public class CommandLineOptions {

    private FileResolver resolver;
    private CommandLine cmd;

    public CommandLineOptions(CommandLine cmd) {
        this.cmd = cmd;
        this.resolver = new FileResolver(root());
    }


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

    /**
     * Resolves the next unparsed argument to a File object if possible.
     * @return File object corresponding to the argument
     */
    public File getFileArgument() {
        if (getNumberOfUnparsedArguments() == 0) {
            // may still be valid in case of help
            return null;
        }
        File file = resolver.resolve(unparsedArguments()[0]);
        if (!file.exists()) {
            //TODO: use central error processing
            System.err.println("Input file (" + file.getAbsolutePath() + ") doesn't exist!");
            System.exit(-1);
        }
        return file;
    }
}
