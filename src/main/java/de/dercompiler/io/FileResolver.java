package de.dercompiler.io;

import de.dercompiler.general.GeneralErrorIds;
import de.dercompiler.io.message.MessageOrigin;

import java.io.File;
import java.net.URISyntaxException;

public class FileResolver {

    private static File cwd = new File("").getAbsoluteFile();

    private File baseLocation;

    /**
     * Constructor, uses cwd as working-directory
     */
    public FileResolver() {
        this(null);
    }

    /**
     * Constructor, sets the working directory for the resolver, depending on the Argument
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
            new OutputMessageHandler(MessageOrigin.GENERAL, System.err).printError(GeneralErrorIds.INVALID_WORKING_DIRECTORY, "Resolved location(" + baseLocation.toString() + ") is not a directory!");
        }
    }

    /**
     * resolve a file based on the working-directory we use locally
     *
     * @param file the relative file location
     * @return the resolved file
     */
    public File resolve(String file) {
        return baseLocation.toPath().resolve(file).toFile();
    }

}
