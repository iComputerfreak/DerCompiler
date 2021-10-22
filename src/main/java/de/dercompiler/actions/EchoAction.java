package de.dercompiler.actions;

import java.io.*;

public class EchoAction extends Action {

    private final File input;

    private final static String HELP_TEXT = "--echo <file>: prints the content of the file to the console.";

    public EchoAction(File input) {
        this.input = input;
    }

    public void run() {
        try (FileInputStream file = new FileInputStream(input)) {
            file.transferTo(System.out);
        } catch (IOException e) {
            e.printStackTrace();
        }
        System.exit(0);
    }

    public void help() {
        System.out.println(HELP_TEXT);
        System.exit(0);
    }
}
