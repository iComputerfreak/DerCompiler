package de.dercompiler.general;

import de.dercompiler.actions.Action;
import de.dercompiler.actions.CompileAction;
import de.dercompiler.actions.EchoAction;
import de.dercompiler.io.CommandLineOptions;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;
import java.util.Objects;

public class CompilerSetup {

    private Action action = null;

    /**
     * this function is called once at startup of the program, based on the parsed Arguments, it sets global states
     *
     * @param options the parsed options
     */
    public static void setupGlobalValues(CommandLineOptions options) {
        OutputMessageHandler.setErrorAsWarning(options.warningsAsError());
        OutputMessageHandler.setPrintStackTrace(options.printStacktrace());

        //sets Value in OutputMessageHandler
        options.resolveColorOutput();
    }

    private void setAction(Action action) {
        if (!Objects.isNull(this.action)) {
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err)
                .printError(GeneralErrorIds.TO_MANY_ACTIONS ,"Actions " + this.action.actionId() + " and " + action.actionId() + " can't be executed at once");
        }
        this.action = action;
    }

    /**
     * parses the action of the program, we assure only one action is active at a time
     *
     * @param options the parsed options
     * @return the active action
     */
    public Action parseAction(CommandLineOptions options) {
        if (options.echo()) {
            File input = options.getFileArgument();
            setAction(new EchoAction(input));
        }

        if (Objects.isNull(action)) {
            File input = options.getFileArgument();
            action = new CompileAction(input);
        }

        return action;
    }
}
