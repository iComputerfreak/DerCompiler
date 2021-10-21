package de.dercompiler.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class EchoAction extends Action {

    private final File input;

    private final static String HELP_TEXT = "--echo <file>: prints the content of the file to the console.";

    public EchoAction(File input) {
        this.input = input;
    }

    public void run() {
        try (BufferedReader br = new BufferedReader(new FileReader(input))) {
            String line;
            while ((line = br.readLine()) != null) {
                System.out.println(line);
            }
        } catch (IOException e) {
            System.out.println("Error while reading echoing file!");
            //TODO: Use central error processing
            System.exit(-1);
        }
        System.exit(0);
    }

    public void help() {
        System.out.println(HELP_TEXT);
        System.exit(0);
    }
}
