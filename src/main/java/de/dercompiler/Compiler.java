
package de.dercompiler;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import de.dercompiler.actions.Action;
import de.dercompiler.actions.CompileAction;
import de.dercompiler.actions.EchoAction;
import de.dercompiler.general.CompilerSetup;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.CommandLineOptions;

import java.io.File;

public class Compiler {

    public static void main(String[] args){
        CommandLineBuilder clb = new CommandLineBuilder();
        clb.parseArguments(args);

        CommandLineOptions options = clb.parseArguments(args);

        CompilerSetup.setupGlobalValues(options);
        Action action = new CompilerSetup().parseAction(options);

        boolean showHelp = options.help();

        // Now, all arguments should be processed.
        options.finish();

        if (showHelp) {
            action.help();
        } else {
            action.run();
        }
        System.exit(0);
    }
}










