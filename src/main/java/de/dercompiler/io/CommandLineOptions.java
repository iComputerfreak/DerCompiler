package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.general.GeneralWarningIds;
import de.dercompiler.io.message.MessageOrigin;
import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.Option;

import java.io.File;
import java.util.LinkedList;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;

import static de.dercompiler.io.CommandLineStrings.*;

/**
 * The central location to get all configurations of the current instance
 */
public class CommandLineOptions {

    private final FileResolver resolver;
    private final CommandLine cmd;
    private final ListIterator<String> unparsedArguments;

    /**
     * Creates new CommandLineOptions using the given CommandLine
     *
     * @param cmd The parsed commandline arguments from common-cli
     */
    public CommandLineOptions(CommandLine cmd) {
        this.cmd = cmd;
        this.resolver = new FileResolver(root());
        this.unparsedArguments = cmd.getArgList().listIterator();
    }

    /**
     * @return The current working-directory
     */
    public String root() {
        if (cmd.hasOption(COMMAND_WORKING_DIR)) {
            return cmd.getOptionValue(COMMAND_WORKING_DIR);
        }
        return null;
    }

    /**
     * @return true if the echo command has been given
     */
    public boolean echo() {
        return cmd.hasOption(COMMAND_ECHO);
    }

    /**
     * @return true if the lextest command has been given
     */
    public boolean lexTest() {
        return cmd.hasOption(COMMAND_LEX_TEST);
    }

    /**
     * @return true if -s option has been given
     */
    public boolean lexString() {
        return cmd.hasOption(OPTION_LEX_STRING_SHORT);
    }

    /**
     * @return true if the parsetest command has been given
     */
    public boolean parseTest() {
        return cmd.hasOption(COMMAND_PARSE_TEST);
    }

    /**
     * @return true if print-ast command has been given
     */
    public boolean printAst() {
        return cmd.hasOption(COMMAND_PRINT_AST);
    }

    /**
     * return true if check command been given
     */
    public boolean check() { return cmd.hasOption(COMMAND_CHECK); }

    /**
     * return true, if no-main is passed as option
     */
    public boolean noMain() { return cmd.hasOption(OPTION_NO_MAIN); }

    /**
     * returns true, if compile-firm is passed as option
     */
    public boolean compileFirm() { return cmd.hasOption(COMMAND_COMPILE_FIRM); }

    /*
     * returns true, if compile ist passes as option
     */
    public boolean compile() { return cmd.hasOption(COMMAND_COMPILE); }

    /**
     * @return true if the lexTest printPosition option has been given
     */
    public boolean printPosition() {
        return cmd.hasOption(OPTION_PRINT_POSITION);
    }

    /**
     * @return true if the parsetest pretty-print option has been given
     */
    public boolean prettyPrint() {
        return cmd.hasOption(OPTION_PRETTY_PRINT);
    }

    /**
     * @return true if the help command has been given
     */
    public boolean help() { return cmd.hasOption(COMMAND_HELP); }

    public boolean time() { return cmd.hasOption(OPTION_TIME_EXECUTION); }

    public boolean noInfo() { return cmd.hasOption(OPTION_NO_INFO); }

    public boolean dumpGraph() { return cmd.hasOption(OPTION_DUMP_GRAPH); }

    public boolean basicOptimization() {
        return cmd.hasOption(OPTION_OPTIMIZE);
    }

    public boolean optimizationsActive() {
        return cmd.hasOption(OPTION_OPTIMIZATION_ON) || !cmd.hasOption(OPTION_OPTIMIZATION_OFF);
    }

    public String getOptionArgument(String optionName) {
        return cmd.getOptionValue(optionName);
    }

    /**
     * @return true if warnings should be treated as error
     */
    public boolean warningsAsError() { return cmd.hasOption(COMMAND_WARNING_AS_ERRORS); }

    /**
     * @return true if the stacktrace of exceptions should be printed
     */
    public boolean printStacktrace() { return cmd.hasOption(COMMAND_PRINT_STACKTRACE); }

    /**
     * @return true, if the pipeline should be printed
     */
    public boolean printPipeline() { return cmd.hasOption(OPTION_PRINT_PIPELINE); }

    public String getCompiler() {
        String option = getActiveOption(OPTION_CLANG, OPTION_GCC, OPTION_CL);
        if (option == null) return null;
        return cmd.getOptionValue(option);
    }

    public String getAssembler() {
        String option = getActiveOption(OPTION_NASM, OPTION_GCC);
        if (option == null) return null;
        return cmd.getOptionValue(option);
    }

    public String getLinker() {
        String option = getActiveOption(OPTION_LD, OPTION_LLD, OPTION_CL);
        if (option == null) return null;
        return cmd.getOptionValue(option);
    }

    /**
     * Sets the global state for the color output
     */
    public void resolveColorOutput() {
        //don't print warning message, because may first want to set a color mode
        String option = getActiveOption(false, COMMAND_PRINT_NO_COLOR, COMMAND_PRINT_ANSI_COLOR, COMMAND_PRINT_8BIT_COLOR, COMMAND_PRINT_TRUE_COLOR);
        if (Objects.isNull(option)) return;
        switch (option) {
            case COMMAND_PRINT_NO_COLOR -> OutputMessageHandler.useNoColors();
            case COMMAND_PRINT_ANSI_COLOR -> OutputMessageHandler.useANSIColors();
            case COMMAND_PRINT_8BIT_COLOR -> OutputMessageHandler.use8BitColors();
            case COMMAND_PRINT_TRUE_COLOR -> OutputMessageHandler.use24BitColors();
        }
        // Now if we have a warning, we will print it
        getActiveOption(COMMAND_PRINT_NO_COLOR, COMMAND_PRINT_ANSI_COLOR, COMMAND_PRINT_8BIT_COLOR, COMMAND_PRINT_TRUE_COLOR);
    }

    /**
     * @return The next argument, that has no fitting option, if none is left return null
     */
    public String getNextUnparsedArgument() {
        return this.hasUnparsedArgumentLeft() ? this.unparsedArguments.next() : null;
    }

    /**
     * @return true, if there is an argument left, that isn't currently processed
     */
    private boolean hasUnparsedArgumentLeft() {
        return this.unparsedArguments.hasNext();
    }

    /**
     * Resolves the next option's argument to a File object if possible.
     * @return The File object corresponding to the argument
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
            new OutputMessageHandler(MessageOrigin.GENERAL)
                .printErrorAndExit(GeneralErrorIds.IO_EXCEPTION, "Input file (" + file.getAbsolutePath() + ") doesn't exist!");
        }
        return file;
    }

    /**
     * Resolves the next unparsed argument to a File object if possible.
     * @return The File object corresponding to the argument
     */
    public File getFileArgument() {
        return this.getFileArgument(null);
    }

    public String getStringArgument(String optionName) {
        String arg;
        if (!Objects.isNull(optionName)) {
            arg = cmd.getOptionValue(optionName);
        } else {
            arg = this.getNextUnparsedArgument();
        }

        if (Objects.isNull(arg)) {
            new OutputMessageHandler(MessageOrigin.GENERAL).printErrorAndExit(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, "Missing String argument");
        }

        return arg;
    }

    public String getStringArgument() {
        return this.getStringArgument(null);
    }

    /**
     * Checks that no arguments are unused
     */
    public void finish() {
        if (this.hasUnparsedArgumentLeft()) {
            StringBuilder sb = new StringBuilder();
            sb.append("Too many arguments. The following arguments could not be processed:");
            this.unparsedArguments.forEachRemaining(s -> sb.append("\n - " + s));
            new OutputMessageHandler(MessageOrigin.GENERAL)
                    .printWarning(GeneralWarningIds.INVALID_COMMAND_LINE_ARGUMENTS, sb.toString());
        }
    }

    /**
     * Checks if there is more than one option given
     * @param printError Whether to print a warning if there is more than one option
     * @param options The options to check
     * @return The active option, if there is only one; null if there are multiple
     */
    private String getActiveOption(boolean printError, String... options) {
        List<String> active = new LinkedList<>();
        for(String option : options) {
            if (cmd.hasOption(option)) {
                active.add(option);
            }
        }
        if (printError && active.size() > 1) {
            StringBuilder sb = new StringBuilder();
            sb.append("More than one option:\n");
            for (String option : active) {
                sb.append("  --" + option + "\n");
            }

            new OutputMessageHandler(MessageOrigin.GENERAL)
                    .printWarning(GeneralWarningIds.INVALID_COMMAND_LINE_ARGUMENTS, sb.toString());

        }
        return active.size() == 0 ? null : active.get(0);
    }

    /**
     * Checks if there is more than one option given and prints a warning if not
     * @param options The options to check
     * @return The active option, if there is only one; null if there are multiple
     */
    private String getActiveOption(String... options) {
        return getActiveOption(true, options);
    }

    public String getActiveParseTestOption() {
        return getActiveOption(OPTION_PARSE_METHOD, OPTION_PARSE_STATEMENT, OPTION_PARSE_EXPRESSION);
    }

    public File getCwd() {
        return resolver.getCwd();
    }

}
