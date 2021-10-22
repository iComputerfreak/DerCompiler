package de.dercompiler.io;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.File;
import java.util.List;
import java.util.ListIterator;

import static de.dercompiler.io.CommandLineStrings.*;

public class CommandLineOptions {

    private FileResolver resolver;
    private CommandLine cmd;
    private ListIterator<String> unparsedArguments;

    public CommandLineOptions(CommandLine cmd) {
        this.cmd = cmd;
        this.resolver = new FileResolver(root());
        this.unparsedArguments = cmd.getArgList().listIterator();
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

    public String[] getUnparsedArguments() {
        return cmd.getArgs();
    }

    public List<String> getUnparsedArgumentsList() {
        return cmd.getArgList();
    }

    public long getNumberOfUnparsedArguments() {
        return cmd.getArgList().size();
    }

    public String getNextUnparsedArgument() {
        return this.hasUnparsedArgumentLeft() ? this.unparsedArguments.next() : null;
    }

    private boolean hasUnparsedArgumentLeft() {
        return this.unparsedArguments.hasNext();
    }

    /**
     * Resolves the next option's argument to a File object if possible.
     * @return File object corresponding to the argument
     */
    public File getFileArgument(Option option) {
        String path;
        if (option != null) {
            path = option.getValue();
        } else {
            path = this.getNextUnparsedArgument();
        }

        if (path == null) {
            // may still be valid in case of help
            return null;
        }

        File file = resolver.resolve(path);
        if (!file.exists()) {
            //TODO: use central error processing
            System.err.println("Input file (" + file.getAbsolutePath() + ") doesn't exist!");
            System.exit(-1);
        }
        return file;
    }

    /**
     * Resolves the next unparsed argument to a File object if possible.
     * @return File object corresponding to the argument
     */
    public File getFileArgument() {
        return this.getFileArgument(null);
    }

    public void finish() {
        if (this.hasUnparsedArgumentLeft()) {
            //TODO: use central error processing
            System.err.println("Too many arguments. The following arguments could not be processed:");
            this.unparsedArguments.forEachRemaining(System.out::println);
            System.exit(-1);
        }
    }
}
