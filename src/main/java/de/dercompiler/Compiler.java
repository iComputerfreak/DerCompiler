
package de.dercompiler;

import de.dercompiler.actions.Action;
import de.dercompiler.general.CompilerSetup;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.CommandLineOptions;
import de.dercompiler.util.ErrorStatus;
import firm.Firm;

public class Compiler {

    public static void main(String[] args){
        // TODO: How to get target triple dynamically?
        // TODO: What does PIC do?
        Firm.init(null, new String[]{ "pic=1" });
        System.out.println("Initialized libFirm Version: " + Firm.getMinorVersion() + "." + Firm.getMajorVersion());
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
        ErrorStatus.exitProgram();
    }
}










