
package de.dercompiler;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import de.dercompiler.actions.Action;
import de.dercompiler.actions.CompileAction;
import de.dercompiler.actions.EchoAction;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.CommandLineOptions;

import java.io.File;

public class Compiler {

    private static final String compilerName = "DerCompiler";

    public static void main(String[] args){
        CommandLineBuilder clb = new CommandLineBuilder();
        clb.parseArguments(args);

        CommandLineOptions options = clb.parseArguments(args);

        Action action = null;

        boolean showHelp = options.help();

        if (options.echo()) {
            if (action != null) {
                System.err.println("Error: Too many actions; can only do one action.");
            }
            File input = options.getFileArgument();
            action = new EchoAction(input);
        }

        if (action == null) {
            File input = options.getFileArgument();
            action = new CompileAction(input);
        }

        if (showHelp) {
            action.help();
        } else {
            action.run();
        }

        System.exit(0);
    }
}










