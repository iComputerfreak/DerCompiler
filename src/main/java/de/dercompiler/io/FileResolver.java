package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;

public class FileResolver {

    private static final File cwd = new File("").getAbsoluteFile();

    private final File baseLocation;

    /**
     * Creates a new FileResolver, using the current working directory
     */
    public FileResolver() {
        this(null);
    }

    /**
     * Creates a new FileResolver using the given working directory
     *
     * @param path if path == null, we use cwd as working-directory
     *             if path is absolute, we use it directly as working-directory
     *             if path is relative, we try to resolve the folder and use it
     */
    public FileResolver(String path) {
        if (path == null) {
            baseLocation = cwd;
        } else {
            File pathFile = new File(path);
            if (pathFile.isAbsolute()) {
                baseLocation = pathFile;
            } else {
                baseLocation = cwd.toPath().resolve(path).toFile();
            }
        }
        if (!baseLocation.isDirectory()) {
            new OutputMessageHandler(MessageOrigin.GENERAL).printErrorAndExit(GeneralErrorIds.INVALID_WORKING_DIRECTORY, "Resolved location(" + baseLocation + ") is not a directory!");
        }
    }

    /**
     * Resolve a file based on the working-directory we use locally
     *
     * @param file The relative file location
     * @return The resolved file
     */
    public File resolve(String file) {
        return baseLocation.toPath().resolve(file).toFile();
    }

}
