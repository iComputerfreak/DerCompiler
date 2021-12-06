package de.dercompiler.linker;

import de.dercompiler.io.FileResolver;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.IOException;

public class Runner {

    private String[] command;

    public static File cwd;

    private BufferedInputStream out;
    private BufferedInputStream err;

    public static void setCwd(File dir) {
        cwd = dir;
    }


    public Runner(String command, String[] options) {
        this.command = new String[options.length + 1];
        this.command[0] = command;
        for (int i = 0; i < options.length; i++) {
            this.command[i + 1] = options[i];
        }
        out = null;
        err = null;
    }

    public boolean run() {
        try {
            Process proc = Runtime.getRuntime().exec(command, new String[0] ,cwd);
            out = new BufferedInputStream(proc.getInputStream());
            err = new BufferedInputStream(proc.getErrorStream());
            proc.waitFor();
            if (proc.exitValue() != 0) return false;

            return true;

        } catch (IOException | InterruptedException e) {
            return false;
        }
    }

    public BufferedInputStream getStdOut() {
        return out;
    }

    public BufferedInputStream getStdErr() {
        return err;
    }

}
