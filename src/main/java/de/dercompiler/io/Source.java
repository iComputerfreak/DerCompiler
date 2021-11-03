package de.dercompiler.io;

import java.io.*;

public abstract class Source {

    protected Reader reader;

    /**
     * @return a new Reader for the Source, closing any previously created Readers for this Source.
     */
    public abstract Reader getNewReader();

    public static Source forFile(File file) {
        return new FileSource(file);
    }

    public static Source forString(String s) {
        return new StringSource(s);
    }

    public boolean isOpen() {
        return reader != null;
    }

}

class FileSource extends Source {

    private final File file;

    public FileSource(File file) {
        this.file = file;
    }

    @Override
    public Reader getNewReader() {
        try {
            if (isOpen()) reader.close();
            reader = new FileReader(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return reader;
    }

}

class StringSource extends Source {

    private final String input;

    public StringSource(String input) {
        this.input = input;
    }

    @Override
    public Reader getNewReader() {
        if (isOpen()) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reader = new StringReader(this.input);
        return reader;
    }
}
