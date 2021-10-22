package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.general.GeneralWarningIds;
import de.dercompiler.io.message.IErrorIds;
import de.dercompiler.io.message.MessageOrigin;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.ListIterator;
import java.util.function.Consumer;

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

    public boolean warningsAsError() { return cmd.hasOption(COMMAND_WARNING_AS_ERRORS); }

    public boolean printStacktrace() { return cmd.hasOption(COMMAND_PRINT_STACKTRACE); }
    
    public String[] getUnparsedArguments() {
        return cmd.getArgs();
    }

    public List<String> getUnparsedArgumentsList() {
        return cmd.getArgList();
    }

    public long getNumberOfUnparsedArguments() {
        return cmd.getArgList().size();
    }

    public void resolveColorOutput() {
        //don't print warning message, because may first want to set a color mode
        String option = hasMoreThanOneOption(false, COMMAND_PRINT_NO_COLOR, COMMAND_PRINT_ANSI_COLOR, COMMAND_PRINT_8BIT_COLOR, COMMAND_PRINT_TRUE_COLOR);
        if (Objects.isNull(option)) return;
        switch (option) {
            case COMMAND_PRINT_NO_COLOR: {
                OutputMessageHandler.useNoColors();
            } break;
            case COMMAND_PRINT_ANSI_COLOR: {
                OutputMessageHandler.useANSIColors();
            } break;
            case COMMAND_PRINT_8BIT_COLOR: {
                OutputMessageHandler.use8BitColors();
            } break;
            case COMMAND_PRINT_TRUE_COLOR: {
                OutputMessageHandler.use24BitColors();
            } break;
        }
        //now if we have a warning, we will print it
        hasMoreThanOneOption(COMMAND_PRINT_NO_COLOR, COMMAND_PRINT_ANSI_COLOR, COMMAND_PRINT_8BIT_COLOR, COMMAND_PRINT_TRUE_COLOR);
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
            OutputMessageHandler omh = new OutputMessageHandler(MessageOrigin.GENERAL, System.err);
            omh.printError(GeneralErrorIds.IO_EXCEPTION, "Input file (" + file.getAbsolutePath() + ") doesn't exist!");
        }
        return file;
    }

    private String hasMoreThanOneOption(boolean printError, String... options) {
        List<String> active = new LinkedList<>();
        for(String option : options) {
            if (cmd.hasOption(option)) {
                active.add(option);
            }
        }
        if (active.size() > 1 && printError) {
            OutputMessageHandler omh = new OutputMessageHandler(MessageOrigin.GENERAL, System.out);
            StringBuilder sb = new StringBuilder();
            sb.append("Following Options are overriding each other:\n");
            for (String option : active) {
                sb.append("  --" + option + "\n");
            }
            sb.append("option: --" + active.get(0) + " is used as configuration.");
            omh.printWarning(GeneralWarningIds.INVALID_COMMAND_LINE_ARGUMENTS, sb.toString());
        }
        return active.size() == 0 ? null : active.get(0);
    }

    private String hasMoreThanOneOption(String... options) {
        return hasMoreThanOneOption(true, options);
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
            StringBuilder sb = new StringBuilder();
            sb.append("Too many arguments. The following arguments could not be processed:");
            this.unparsedArguments.forEachRemaining(new Consumer<String>() {
                @Override
                public void accept(String s) {
                    sb.append("\n" + s);
                }
            });
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printError(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, sb.toString());
        }
    }
}
