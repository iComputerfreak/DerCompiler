package de.dercompiler;

import com.diogonunes.jcolor.Ansi;
import com.diogonunes.jcolor.AnsiFormat;
import com.diogonunes.jcolor.Attribute;
import de.dercompiler.io.CommandLineBuilder;
import de.dercompiler.io.CommandLineOptions;
import de.dercompiler.io.FileResolver;

import java.io.*;

public class Compiler {

    private static final String compilerName = "DerCompiler";

    public static void main(String[] args){
        CommandLineBuilder clb = new CommandLineBuilder();
        clb.parseArguments(args);

        CommandLineOptions options = clb.parseArguments(args);

        FileResolver resolver = new FileResolver(options.root());

        if (options.help()) {
            CommandLineBuilder.printHelp(compilerName);
            System.exit(0);
        }

        if (options.echo()) {
            if (options.getNumberOfUnparsedArguments() != 1) {
                System.out.println(options.unparsedArguments()[0] + " " + options.unparsedArguments()[1]);
                System.out.println("No Input-File!");
                System.exit(-1);
            }
            File input = resolver.resolve(options.unparsedArguments()[0]);
            if (!input.exists()) {
                System.out.println("Input file (" + input.getAbsolutePath() + ") doesn't exist!");
                System.exit(-1);
            }
            try (BufferedReader br = new BufferedReader(new FileReader(input))) {
                String line;
                while ((line = br.readLine()) != null) {
                    System.out.println(line);
                }
            } catch (IOException e) {
                System.out.println("Error while reading echoing file!");
                System.exit(-1);
            }
            System.exit(0);
        }

        System.out.println("Hello, Compiler!");

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
    }
}
