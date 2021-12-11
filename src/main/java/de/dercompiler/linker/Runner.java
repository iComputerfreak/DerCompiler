package de.dercompiler.linker;

import de.dercompiler.io.FileResolver;
import de.dercompiler.semantic.type.ArrayType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Runner {

    private final ArrayList<String> command;

    public static File cwd = new FileResolver().getCwd();

    private BufferedInputStream out;
    private BufferedInputStream err;

    public static void setCwd(File dir) {
        cwd = dir;
    }

    public static File getCwd() {
        return cwd;
    }


    public Runner(String command) {
        this.command = new ArrayList<>();
        this.command.add(command);
        out = null;
        err = null;
    }

    public boolean run() {
        try {
            Process proc = Runtime.getRuntime().exec(command.toArray(new String[0]), null ,cwd);
            out = new BufferedInputStream(proc.getInputStream());
            err = new BufferedInputStream(proc.getErrorStream());
            proc.waitFor();
            return proc.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public void append(String option) {
        command.add(option);
    }

    public void append(String[] options) {
        for (String option : options) {
            append(option);
        }
    }

    public BufferedInputStream getStdOut() {
        return out;
    }

    public BufferedInputStream getStdErr() {
        return err;
    }

}
