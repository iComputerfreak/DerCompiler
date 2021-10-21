
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

        System.out.println((char)27 + "[33mYELLOW");

        AnsiFormat ansi = new AnsiFormat(Attribute.CYAN_TEXT(), Attribute.YELLOW_BACK());
        System.out.println(ansi.format("ansi-colors"));

        Attribute textBit8 = Attribute.TEXT_COLOR(196); //red
        Attribute backBit8 = Attribute.BACK_COLOR(92); // lila
        System.out.println(Ansi.colorize("8-bit Color", textBit8, backBit8));

        Attribute text24bit = Attribute.TEXT_COLOR(0, 102, 0);
        Attribute back24bit = Attribute.BACK_COLOR(255, 152, 204);
        System.out.println(Ansi.colorize("24-bit color", text24bit, back24bit));

        System.out.println(ansi.format(Ansi.colorize(Ansi.colorize("mixed color formats", text24bit, back24bit), textBit8, backBit8)));

        /*
        AnsiFormat ansi = new AnsiFormat(Attribute.CYAN_TEXT(), Attribute.YELLOW_BACK());

        ProcessBuilder builder = new ProcessBuilder("cmd", "/C", "echo " + ansi.format("test"));
        builder.redirectErrorStream(true);
        Process process = null;
        try {
            process = builder.start();
            InputStream is = process.getInputStream();
            BufferedReader reader = new BufferedReader(new InputStreamReader(is));

            long count = 0;

            String line = null;
            while ((line = reader.readLine()) != null) {
                count += line.length();
                System.out.println(line);
            }
            System.out.println(count);
        } catch (IOException e) {
            e.printStackTrace();
        }
        */

        System.exit(0);
    }
}










