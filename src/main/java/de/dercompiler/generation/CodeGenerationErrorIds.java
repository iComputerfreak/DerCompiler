package de.dercompiler.generation;

import de.dercompiler.io.message.IErrorIds;

public enum CodeGenerationErrorIds implements IErrorIds {
    COMPILER_NOT_FOUND(100),
    LINKER_NOT_FOUND(150),
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
