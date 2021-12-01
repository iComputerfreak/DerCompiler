package de.dercompiler.transformation;

public class TargetTriple {

    public static final String OS = System.getProperty("os.name").toLowerCase();

    //https://stackoverflow.com/questions/14288185/detecting-windows-or-linux
    public static boolean isWindows() {
        return OS.contains("win");
    }

    public static boolean isLinux() {
        return (OS.contains("nix") || OS.contains("nux") || OS.contains("aix"));
    }

    public static boolean isMacOS() {
        return OS.contains("mac");
    }
}
