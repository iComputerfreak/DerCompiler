package de.dercompiler.general;

import de.dercompiler.actions.Action;
import de.dercompiler.actions.CompileAction;
import de.dercompiler.actions.EchoAction;
import de.dercompiler.actions.LexTestAction;
import de.dercompiler.io.CommandLineOptions;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;
import java.util.Objects;

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
     * @param action The action to set
     */
    private void setAction(Action action) {
        if (!Objects.isNull(this.action)) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err)
                .printErrorAndExit(GeneralErrorIds.TOO_MANY_ACTIONS,"Actions " + this.action.actionId() + " and " + action.actionId() + " cannot be executed at the same time");
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
            File input = options.getFileArgument();
            setAction(new LexTestAction(input));
        }

        if (Objects.isNull(action)) {
            File input = options.getFileArgument();
            action = new CompileAction(input);
        }

        return action;
    }
}
