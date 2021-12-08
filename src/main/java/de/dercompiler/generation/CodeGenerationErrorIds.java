package de.dercompiler.generation;

import de.dercompiler.io.message.IErrorIds;

public enum CodeGenerationErrorIds implements IErrorIds {
    COMPILER_NOT_FOUND(100),
    COMPILER_ERROR(110),

    LINKER_NOT_FOUND(200),
    LINKER_ERROR(210),

    CANT_OUTPUT_FILE(500),

    UNKNOWN_TARGET_TRIPLE(1000),
    UNKNOWN_ASM_STYLE(1010),
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
