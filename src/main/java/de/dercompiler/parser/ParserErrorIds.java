package de.dercompiler.parser;

import de.dercompiler.io.message.IErrorIds;

public enum ParserErrorIds implements IErrorIds {
    UNSUPPORTED_OPERATOR_TOKEN(500),
    UNSUPPORTED_TYPE_TOKEN(510),
    UNSUPPORTED_STATEMENT(520),

    EXPECTED_PRIMARY_EXPRESSION(600),
    EXPECTED_ARGUMENT(610),
    EXPECTED_OBJECT_INSTANTIATION(620),
    EXPECTED_PRIMARY_TYPE(630),
    EXPECTED_CLASS_DECLARATION(640),
    EXPECTED_PUBLIC_KEYWORD(650),
    EXPECTED_BASIC_TYPE(660),
    EXPECTED_TOKEN(670),
    EXPECTED_IDENTIFIER(680),
    EXPECTED_SEMICOLON(690);

    private final int id;

    ParserErrorIds(int id) {
        this.id = id;
    }

    @Override
    public int getId() {
        return this.id;
    }
}
