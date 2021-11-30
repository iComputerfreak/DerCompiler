
package de.dercompiler;

import de.dercompiler.actions.Action;
import de.dercompiler.general.CompilerSetup;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.CommandLineOptions;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.FirmSetup;
import de.dercompiler.transformation.TargetTriple;
import de.dercompiler.util.ErrorStatus;
import firm.Firm;

import java.io.IOException;
import java.lang.management.ManagementFactory;

public class Compiler {

    static final boolean debug = false;


    public static void main(String[] args){
        if (debug && TargetTriple.isWindows()) {
            System.out.println(ManagementFactory.getRuntimeMXBean().getName());
            try {
                System.in.read();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        CommandLineBuilder clb = new CommandLineBuilder();
        clb.parseArguments(args);

        CommandLineOptions options = clb.parseArguments(args);

        CompilerSetup.setupGlobalValues(options);
        Action action = new CompilerSetup().parseAction(options);
        if (debug) {
            FirmSetup.firmSetupDebug();
        } else {
            FirmSetup.firmSetUp();
        }

        Firm.init(null, new String[]{ "pic=1" });
        new OutputMessageHandler(MessageOrigin.GENERAL).printInfo("Initialized libFirm Version: " + Firm.getMinorVersion() + "." + Firm.getMajorVersion());

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










