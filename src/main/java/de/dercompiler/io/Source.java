package de.dercompiler.io;

import java.io.*;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.LexerErrorIds;

public abstract class Source {

    protected BufferedReader reader;

    /**
     * @return a new Reader for the Source, closing any previously created Readers for this Source.
     */
    public abstract BufferedReader getNewReader();

    /**
     * @return a new OutputStream for the Source.
     */
    public abstract InputStream getNewStream();

    public static Source forFile(File file) {
        return new FileSource(file);
    }

    public static Source forString(String s) {
        return new StringSource(s);
    }

    public boolean isOpen() {
        return reader != null;
    }

    @Override
    public abstract String toString();
}

class FileSource extends Source {

    private final File file;

    public FileSource(File file) {
        this.file = file;
    }

    @Override
    public BufferedReader getNewReader() {
        try {
            if (isOpen()) reader.close();
            reader = new BufferedReader(new FileReader(file));
        } catch (IOException e) {
            (new OutputMessageHandler(MessageOrigin.LEXER)).printErrorAndExit(LexerErrorIds.FILE_NOT_FOUND, 
                    "The file does not exist or is not readable", e);
        }
        return reader;
    }

    @Override
    public InputStream getNewStream() {
        try {
            return new BufferedInputStream(new FileInputStream(file));
        } catch (IOException e) {
            (new OutputMessageHandler(MessageOrigin.LEXER)).printErrorAndExit(LexerErrorIds.FILE_NOT_FOUND,
                    "The file does not exist or is not readable", e);
            //we never return
            return null;
        }
    }

    @Override
    public String toString() {
        return file.getName();
    }
}

class StringSource extends Source {

    private final String input;

    public StringSource(String input) {
        this.input = input;
    }

    @Override
    public BufferedReader getNewReader() {
        if (isOpen()) {
            try {
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        reader = new BufferedReader(new StringReader(this.input));
        return reader;
    }

    @Override
    public InputStream getNewStream() {
        return new BufferedInputStream(new ByteArrayInputStream(input.getBytes()));
    }

    @Override
    public String toString() {
        return "<cliStringArgument>";
    }
}
