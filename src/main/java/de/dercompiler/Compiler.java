package de.dercompiler;

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
    }
}
