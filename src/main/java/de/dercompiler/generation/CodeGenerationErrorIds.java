package de.dercompiler.generation;

import de.dercompiler.io.message.IErrorIds;

public enum CodeGenerationErrorIds implements IErrorIds {
    COMPILER_NOT_FOUND(100),
    LINKER_NOT_FOUND(150),

    CANT_OUTPUT_FILE(500),
    ;

    private int id;

    CodeGenerationErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return 0;
    }
}
