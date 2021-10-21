package de.dercompiler.actions;

import java.io.File;

public class CompileAction extends Action {
    private final File input;

    public CompileAction(File input) {
        this.input = input;
    }

    @Override
    public void run() {
        System.out.println("Hallo, Compiler!");
    }

    @Override
    public void help() {
        System.err.println("CompileAction::help is not implemented yet.");
    }
}
