package de.dercompiler.parser;

import de.dercompiler.io.message.IErrorIds;

public enum ParserErrorIds implements IErrorIds {
    TODO(300),

    UNSUPPORTED_OPERATOR_TOKEN(500),

    EXPECTED_PRIMARY_EXPRESSION(600),
    EXPECTED_ARGUMENT(610),
    EXPECTED_OBJECT_INSTANTIATION(620),
    EXPECTED_PRIMARY_TYPE(630),
    EXPECTED_CLASS_DECLARATION(640),
    EXPECTED_PUBLIC_KEYWORD(650),
    EXPECTED_BASIC_TYPE(660),
    
    ;
    
    private final int id;

    ParserErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}