package de.dercompiler.linker;

import de.dercompiler.io.FileResolver;
import de.dercompiler.semantic.type.ArrayType;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;

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
        //ProcessBuilder proc = new ProcessBuilder(command.toArray(new String[0]));
        try {
            //System.out.println(Arrays.toString(command.toArray(new String[0])));
            Process proc = Runtime.getRuntime().exec(command.toArray(new String[0]), null ,cwd);
            proc.waitFor();
            out = new BufferedInputStream(proc.getInputStream());
            err = new BufferedInputStream(proc.getErrorStream());
            return proc.exitValue() == 0;
        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
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
