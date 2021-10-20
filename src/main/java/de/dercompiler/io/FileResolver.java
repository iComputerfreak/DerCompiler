package de.dercompiler.io;

import java.io.File;
import java.net.URISyntaxException;

public class FileResolver {

    private static File jarLocation;

    {
        try {
            jarLocation = new File(FileResolver.class.getProtectionDomain().getCodeSource().getLocation().toURI()).getParentFile();
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private File baseLocation;

    public FileResolver() {
        this(null);
    }

    public FileResolver(String path) {
        if (path == null) {
            baseLocation = jarLocation;
        } else {
            File pathFile = new File(path);
            if (pathFile.isAbsolute()) {
                baseLocation = pathFile;
            } else {
                baseLocation = jarLocation.toPath().resolve(path).toFile();
            }
        }
    }

    public File resolve(String file) {
        return baseLocation.toPath().resolve(file).toFile();
    }

}
