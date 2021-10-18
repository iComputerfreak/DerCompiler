package de.dercompiler.io;

import java.io.File;
import java.net.URISyntaxException;

public class FileResolver {

    private static File jarLocation;

    {
        try {
            jarLocation = new File(FileResolver.class.getProtectionDomain().getCodeSource().getLocation().toURI());
        } catch (URISyntaxException e) {
            e.printStackTrace();
        }
    }

    private File baseLocation;

    public FileResolver() {
        this(null);
    }

    public FileResolver(String relative ) {
        if (relative == null) {
            baseLocation = jarLocation;
        }
        else {
            baseLocation = jarLocation.toPath().resolve(relative).toFile();
        }
    }

    public File resolve(String file) {
        return baseLocation.toPath().resolve(file).toFile();
    }

}
