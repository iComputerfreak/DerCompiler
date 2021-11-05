package de.dercompiler.general;

import de.dercompiler.actions.*;
import de.dercompiler.io.CommandLineOptions;
import de.dercompiler.io.CommandLineStrings;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.Source;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;
import java.util.Objects;

import static de.dercompiler.io.CommandLineStrings.OPTION_PRETTY_PRINT;

public class CompilerSetup {

    /**
     * The action to execute
     */
    private Action action = null;

    /**
     * This function is called once at startup of the program, based on the parsed arguments, it sets global states
     *
     * @param options The parsed command line options
     */
    public static void setupGlobalValues(CommandLineOptions options) {
        OutputMessageHandler.setErrorAsWarning(options.warningsAsError());
        OutputMessageHandler.setPrintStackTrace(options.printStacktrace());

        //sets Value in OutputMessageHandler
        options.resolveColorOutput();
    }

    /**
     * Sets the given action as active for this setup.
     * If there is an action set already, this function prints an error and ignores the given action.
     *
     * @param action The action to set
     */
    private void setAction(Action action) {
        if (!Objects.isNull(this.action)) {
            new OutputMessageHandler(MessageOrigin.GENERAL)
                    .printErrorAndExit(GeneralErrorIds.TOO_MANY_ACTIONS, "Actions " + this.action.actionId() + " and " + action.actionId() + " cannot be executed at the same time");
        }
        this.action = action;
    }

    /**
     * Parses the action of the program, we assure only one action is active at a time
     *
     * @param options The parsed options
     * @return The active action
     */
    public Action parseAction(CommandLineOptions options) {
        if (options.echo()) {
            File input = options.getFileArgument();
            setAction(new EchoAction(input));
        }

        if (options.lexTest()) {
            Source src = getSourceFromArgs(options);
            LexTestAction action = new LexTestAction(src);
            action.setPrintPosition(options.printPosition());
            setAction(action);
        } else if (options.printPosition()) {
            new OutputMessageHandler(MessageOrigin.GENERAL).printErrorAndExit(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, "Invalid argument: --print-position only works with --lextext");
        }

        String parseOption = options.getActiveParseTestOption();
        if (options.parseTest()) {
            Source src = getSourceFromArgs(options);
            String parseTestOption = parseOption;
            ParseTestAction action = new ParseTestAction(src, parseTestOption);
            action.setPrint(options.prettyPrint());
            setAction(action);
        } else {
            if (Objects.isNull(parseOption) && options.prettyPrint()) {
                parseOption = OPTION_PRETTY_PRINT;
            }
            if (!Objects.isNull(parseOption)) {
                new OutputMessageHandler(MessageOrigin.GENERAL).printErrorAndExit(GeneralErrorIds.INVALID_COMMAND_LINE_ARGUMENTS, "Invalid argument: --'%s' only works with --parsetest".formatted(parseOption));
            }
        }

        if (options.printAst()) {
            Source src = getSourceFromArgs(options);
            PrintAstAction action = new PrintAstAction(src);
            setAction(action);
        }

        if (Objects.isNull(action)) {
            File input = options.getFileArgument();
            action = new CompileAction(input);
        }


        return action;
    }

    private Source getSourceFromArgs(CommandLineOptions options) {
        Source src = null;
        if (options.lexString()) {
            String inputString = options.getStringArgument(CommandLineStrings.OPTION_LEX_STRING);
            src = Source.forString(inputString);
        } else {
            File input = options.getFileArgument();

            try {
                src = Source.forFile(input);
            } catch (NullPointerException e) {
                new OutputMessageHandler(MessageOrigin.GENERAL).printErrorAndExit(GeneralErrorIds.MISSING_INPUT_FILE, "An argument is missing its corresponding input", e);
            }
        }
        return src;
    }
}
