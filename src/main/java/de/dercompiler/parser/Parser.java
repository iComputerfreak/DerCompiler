package de.dercompiler.parser;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.SourcePosition;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.*;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import static de.dercompiler.lexer.token.OperatorToken.*;
import static de.dercompiler.lexer.token.Token.*;
import static de.dercompiler.lexer.token.TypeToken.VOID_TYPE;

/**
 * Represents a Parser that consumes tokens from the given lexer to check them for valid syntax
 */
public class Parser {

    Lexer lexer;
    LexerWrapper wlexer;
    PrecedenceParser precedenceParser;
    private final OutputMessageHandler logger;

    /**
     * Creates a new Parser from the given lexer
     * @param lexer The lexer that provides the tokens for this parser
     */
    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.wlexer = new LexerWrapper(lexer);
        this.precedenceParser = new PrecedenceParser(lexer, this);
        this.logger = new OutputMessageHandler(MessageOrigin.PARSER);
    }

    /**
     * Parses a complete program from the given lexer. This is the starting point for parsing MiniJava files.
     * @return The parsed {@link Program}
     */
    public Program parseProgram() {
        // ClassDeclaration*
        SourcePosition pos = lexer.peek().position();
        List<ClassDeclaration> classes = new ArrayList<>();
        AnchorSet ank = new AnchorSet();
        while (lexer.peek().type() == CLASS) {
            ClassDeclaration cd = parseClassDeclaration(ank.fork(CLASS));
            if (!Objects.isNull(cd)) {
                classes.add(cd);
            }
        }
        TokenOccurrence next = lexer.nextToken();
        if (next.type() != EOF) {
            logger.printParserError(ParserErrorIds.EXPECTED_CLASS_DECLARATION, "Expected class declaration, but found " + next.type(), lexer, lexer.peek().position());
        }
        return new Program(pos, classes);
    }

    /**
     * Parses a {@link ClassDeclaration} by consuming the necessary tokens from the lexer
     */
    public ClassDeclaration parseClassDeclaration(AnchorSet ank) {
        // class IDENT { ClassMember* }
        SourcePosition pos = lexer.peek().position();
        IdentifierToken identifier;
        try {
            expect(CLASS, ank);
            identifier = expectIdentifier(ank);
            expect(L_CURLY_BRACKET, ank);
        } catch (ExpectedTokenError e) {
            return null;
        }
        List<ClassMember> members = new ArrayList<>();
        // While our next token is not the '}' token
        while (lexer.peek().type() != R_CURLY_BRACKET && lexer.peek().type() != EOF) {
            members.add(parseClassMember(ank.fork(PUBLIC)));
        }
        // Consume the R_CURLY_BRACKET
        SourcePosition pos2 = lexer.peek().position();
        try {
            expect(R_CURLY_BRACKET, ank);
        } catch (ExpectedTokenError e) {
            members.add(new ErrorClassMember(pos2));
        }
        return new ClassDeclaration(pos, identifier.getIdentifier(), members);
    }
    
    /**
     * Parses a {@link ClassMember} by consuming the necessary tokens from the lexer
     */
    public ClassMember parseClassMember(AnchorSet ank) {
        // MainMethod ->    public static void IDENT ( Type IDENT )
        // Field ->         public Type IDENT ;
        // Method ->        public Type IDENT ( Parameters? ) MethodRest? Block
        SourcePosition pos = lexer.peek().position();
        if (lexer.peek().type() != PUBLIC) {
            logger.printParserError(ParserErrorIds.EXPECTED_PUBLIC_KEYWORD, "Expected 'public' keyword but found '%s'".formatted(lexer.peek()), lexer, pos);
            return new ErrorClassMember(pos);
        }

        // MainMethod
        if (lexer.peek(1).type() == STATIC) {
            return parseMainMethod(ank);
        }

        // Now we have to decide if we need to parse Field or Method
        // Since the Type can have unlimited tokens (due to array types), we need to parse the full type now
        // First we consume the public token that is still remaining
        Type type;
        IdentifierToken identifier;
        try {
            expect(PUBLIC, ank);
            type = parseType(ank);
            identifier = expectIdentifier(ank);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }
        // Now we decide which class member to parse
        // Field
        if (lexer.peek().type() == SEMICOLON) {
            return parseField(ank, type, identifier);
        }
        // Method
        if (lexer.peek().type() == L_PAREN) {
            return parseMethod(ank, type, identifier);
        }


        skipToAnker(ank);
        logger.printParserError(ParserErrorIds.EXPECTED_SEMICOLON, "Expected semicolon but found '%s'".formatted(lexer.peek(3).type()), lexer, lexer.peek(3).position());
        return new ErrorClassMember(pos);
    }

    /**
     * Parses a {@link Field} by consuming the necessary tokens from the lexer.
     * For this function, the type and identifier of the field is assumed to be already consumed before calling.
     * @param type The type of the field
     * @param identifier The identifier of the field
     * @return The parsed field
     */
    public ClassMember parseField(AnchorSet ank, Type type, IdentifierToken identifier) {
        // We already parsed "public Type IDENT"
        // public Type IDENT ;
        SourcePosition pos = lexer.peek().position();
        try {
            expect(SEMICOLON, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }
        return new Field(pos, type, identifier.getIdentifier());
    }
    
    /**
     * Parses a {@link MainMethod} by consuming the necessary tokens from the lexer
     */
    public ClassMember parseMainMethod(AnchorSet ank) {
        // public static void IDENT ( Type IDENT ) MethodRest? Block
        SourcePosition pos = lexer.peek().position();
        IdentifierToken name;
        Type paramType;
        IdentifierToken paramName;
        try {
            expect(PUBLIC, ank);
            expect(STATIC, ank);
            expect(VOID_TYPE, ank);
            name = expectIdentifier(ank);
            expect(L_PAREN, ank);
            paramType = parseType(ank);
            paramName = expectIdentifier(ank);
            expect(R_PAREN, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }

        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest(ank.fork(L_CURLY_BRACKET));
        }
        BasicBlock block = parseBasicBlock(ank);
        return new MainMethod(pos, name.getIdentifier(), paramType, paramName.getIdentifier(), methodRest, block);
    }

    /**
     * Parses a {@link Method} by consuming the necessary tokens from the lexer
     * This function assumes that the type and identifier have not been consumed beforehand.
     * See also: {@link Parser#parseMethod}
     */
    public ClassMember parseFullMethod(AnchorSet ank) {
        SourcePosition pos = lexer.peek().position();
        Type type;
        IdentifierToken identifier;
        try {
            expect(PUBLIC, ank);
            type = parseType(ank);
            identifier = expectIdentifier(ank);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }

        return parseMethod(ank, type, identifier);
    }

    /**
     * Parses a {@link Method} by consuming the necessary tokens from the lexer.
     * For this function, the type and identifier of the field is assumed to be already consumed before calling.
     * @param type The type of the method
     * @param identifier The identifier of the method
     * @return The parsed method
     */
    public ClassMember parseMethod(AnchorSet ank, Type type, IdentifierToken identifier) {
        // We already parsed "public Type IDENT"
        // public Type IDENT ( Parameters? ) MethodRest? Block\
        SourcePosition pos = lexer.peek().position();
        LinkedList<Parameter> params;
        AnchorSet paramsAnk = ank.fork(L_PAREN);
        try {
            expect(L_PAREN, paramsAnk);
            // Check if we have parameters
            params = new LinkedList<>();
            // Parse the first argument
            if (lexer.peek().type() != R_PAREN) {
                params.add(parseParameter(ank.fork(COMMA)));
            }
            // If we have more arguments, they are each prefixed by a COMMA
            while (lexer.peek().type() == COMMA) {
                expect(COMMA, paramsAnk);
                params.add(parseParameter(ank.fork(COMMA)));
            }
            expect(R_PAREN, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }
        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest(ank.fork(L_CURLY_BRACKET));
        }
        BasicBlock block = parseBasicBlock(ank);
        return new Method(pos, type, identifier.getIdentifier(), params, methodRest, block);
    }

    /**
     * Parses a {@link MethodRest} by consuming the necessary tokens from the lexer
     */
    public MethodRest parseMethodRest(AnchorSet ank) {
        // throws IDENT
        SourcePosition pos;
        IdentifierToken ident;
        try {
            expect(THROWS, ank);
            pos = lexer.peek().position();
            ident = expectIdentifier(ank);
        } catch (ExpectedTokenError e) {
            //in this case null is okay, because it may also be null if it is not present
            return null;
        }
        return new MethodRest(pos, ident.getIdentifier());
    }
    
    /**
     * Parses a {@link Parameter} by consuming the necessary tokens from the lexer
     */
    public Parameter parseParameter(AnchorSet ank) {
        // Type IDENT
        SourcePosition pos = lexer.peek().position();
        Type type = parseType(ank.fork().addIdent());
        IdentifierToken ident;
        try {
            ident = expectIdentifier(ank);
        } catch (ExpectedTokenError e) {
            return null;
        }
        return new Parameter(pos, type, ident.getIdentifier());
    }
    
    /**
     * Parses a {@link Type} by consuming the necessary tokens from the lexer
     */
    public Type parseType(AnchorSet ank) {
        // BasicType TypeRest
        SourcePosition pos = lexer.peek().position();
        BasicType type = parseBasicType();
        int dimension = parseTypeRest(ank);
        return new Type(pos, type, dimension);
    }

    /**
     * Parses the array dimension of a type by consuming the necessary tokens from the lexer
     */
    public int parseTypeRest(AnchorSet ank) {
        // ([] TypeRest)?
        if (lexer.peek().type() == L_SQUARE_BRACKET) {
            try {
                expect(L_SQUARE_BRACKET, ank);
                expect(R_SQUARE_BRACKET, ank);
            } catch (ExpectedTokenError e) {
                return -1;
            }
            return parseTypeRest(ank) + 1;
        }
        // If there is no rest, we return null
        return 0;
    }

    /**
     * Parses a {@link BasicType} by consuming the necessary tokens from the lexer
     */
    public BasicType parseBasicType() {
        // int | boolean | void | IDENT
        SourcePosition pos = lexer.peek().position();
        IToken t = lexer.nextToken().type();
        if (t instanceof IdentifierToken ident) {
            return new CustomType(pos, ident.getIdentifier());
        }
        if (t instanceof TypeToken type) {
            switch (type) {
                case INT_TYPE:
                    return new IntType(pos);
                case BOOLEAN_TYPE:
                    return new BooleanType(pos);
                case VOID_TYPE:
                    return new VoidType(pos);
                case BYTE_TYPE, SHORT_TYPE, LONG_TYPE, FLOAT_TYPE, DOUBLE_TYPE, CHARACTER_TYPE:
                    logger.printParserError(ParserErrorIds.UNSUPPORTED_TYPE_TOKEN, "Type '%s' is not supported".formatted(type), lexer, pos);
                    return new ErrorType(pos);
            }
        }

        logger.printParserError(ParserErrorIds.EXPECTED_BASIC_TYPE,
                "Expected 'int', 'boolean', 'void' or an identifier, but got '%s'".formatted(t), lexer, pos);
        return new ErrorType(pos);
    }

    /**
     * Checks whether the lexer's next token matches the given token and consumes it.
     *
     * @param expected The token to check for
     * @throws ExpectedTokenError If the current token does not match the expected token
     */
    private void expect(IToken expected, AnchorSet ank) throws ExpectedTokenError {
        TokenOccurrence t = lexer.peek();
        if (t.type() != expected) {
            //don't print if current is in ank, then we may have already printed an error
            if (!ank.hasToken(t.type()) || t.type() == EOF) {
                logger.printParserError(ParserErrorIds.EXPECTED_TOKEN, "Expected %s but found '%s'".formatted(expected, t.type()), lexer, t.position());
            }
            ExpectedTokenError e = new ExpectedTokenError("Expected %s but found '%s'".formatted(expected, t.type()));
            IToken token = wlexer.peek();
            while (!ank.hasToken(token)) {
                token = wlexer.nextToken();
            }
            throw e;
        }
        //consume if it was the expected one, otherwise out current token may be in the ancher
        lexer.nextToken();
    }

    /**
     * Checks if the lexer's next token is an identifier and consumes it.
     *
     * @return The parsed IdentifierToken
     * @throws ExpectedTokenError If the current token is not an identifier
     */
    private IdentifierToken expectIdentifier(AnchorSet ank) throws ExpectedTokenError {
        IToken token = lexer.peek().type();
        if (token instanceof IdentifierToken it) {
            lexer.nextToken();
            return it;
        }
        //don't print if current is in ank, then we may have already printed an error
        if (!ank.hasToken(token) || token == EOF) {
            logger.printParserError(ParserErrorIds.EXPECTED_IDENTIFIER, "Identifier expected, but found '%s'".formatted(token.toString()), lexer, lexer.peek().position());
        }
        ExpectedTokenError e = new ExpectedTokenError("Identifier expected, but found '%s'".formatted(token.toString()));
        while (!ank.hasToken(token)) {
            token = wlexer.nextToken();
        }
        throw e;
    }

    private void skipToAnker(AnchorSet ank) {
        IToken token = wlexer.peek();
        while (!ank.hasToken(token)) token = wlexer.nextToken();
    }

    // From here on, we use wlexer instead of lexer

    private boolean isBlockStatement(IToken token) {
        return isType(token) || isExpression(token) || token instanceof Token t && switch (t) {
            case L_CURLY_BRACKET, SEMICOLON, IF, WHILE, RETURN -> true;
            // accept them now in order to get more useful error messages later
            case FOR, SWITCH, DO -> true;
            default -> false;
        };
    }

    public BasicBlock parseBasicBlock(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        LinkedList<Statement> statements = new LinkedList<>();
        try {
            expect(L_CURLY_BRACKET, ank);
        } catch (ExpectedTokenError e) {
            statements.addLast(new ErrorStatement(pos));
            return new BasicBlock(pos, statements);
        }
        while (isBlockStatement(wlexer.peek())) {
            statements.addLast(parseBlockStatement(ank.fork(L_CURLY_BRACKET)));
        }
        SourcePosition pos2 = lexer.peek().position();
        try {
            expect(R_CURLY_BRACKET, ank);
        } catch (ExpectedTokenError e) {
            //one off, but this is okay, because we never read it?!?
            statements.addLast(new ErrorStatement(pos2));
            return new BasicBlock(pos, statements);
        }
        return new BasicBlock(pos, statements);
    }

    public Statement parseBlockStatement(AnchorSet ank) {
        IToken token = wlexer.peek();
        Statement statement;
        boolean possible_expression = isExpression(token);
        boolean possible_type = isType(token);
        //= token instanceof IdentifierToken
        if (possible_expression && possible_type) {
            //when ident[] varname -> variableDeclaration
            //when ident[expr] -> expression
            if (wlexer.peek(1) instanceof IdentifierToken || (wlexer.peek(1) == L_SQUARE_BRACKET && wlexer.peek(2) == R_SQUARE_BRACKET)) {
                statement = parseVariableDeclaration(ank);
            } else {
                statement = parseStatement(ank);
            }
        } else if (possible_type) {
            statement = parseVariableDeclaration(ank);
        } else {
            // handles statement possible_expression and non_primary
            statement = parseStatement(ank);
        }
        return statement;
    }

    public Statement parseVariableDeclaration(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        Type type = parseType(ank);
        IdentifierToken ident;
        Expression expression;
        try {
            ident = expectIdentifier(ank);
            expression = new UninitializedValue(pos);
            if (wlexer.peek() == ASSIGN) {
                expect(ASSIGN, ank);
                expression = parseExpression(ank.fork(SEMICOLON));
            }
            expect(SEMICOLON, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        return new LocalVariableDeclarationStatement(pos, type, ident.getIdentifier(), expression);
    }

    public Statement parseStatement(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        IToken token = wlexer.peek();
        if (token instanceof Token t) {
            return switch (t) {
                case L_CURLY_BRACKET -> parseBasicBlock(ank);
                case SEMICOLON -> wlexer.consumeToken(new EmptyStatement(pos));
                case IF -> parseIfStatement(ank);
                case WHILE -> parseWhileStatement(ank);
                case RETURN -> parseReturnStatement(ank);
                case FOR, DO, SWITCH -> {
                    logger.printParserError(ParserErrorIds.UNSUPPORTED_STATEMENT, "'%s' statements are not supported".formatted(t), lexer, pos);
                    yield new ErrorStatement(pos);
                }
                default -> parseExpressionStatement(ank);
            };
        }
        return parseExpressionStatement(ank);
    }

    public Statement parseIfStatement(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        Expression condition;
        Statement thenStatement;
        Statement elseStatement;
        try {
            expect(IF, ank);
            expect(L_PAREN, ank);
            condition = parseExpression(ank.fork(R_PAREN));
            expect(R_PAREN, ank);
            thenStatement = parseStatement(ank.fork(ELSE));
            elseStatement = null;
            if (wlexer.peek() == ELSE) {
                expect(ELSE, ank);
                elseStatement = parseStatement(ank);
            }
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        return new IfStatement(pos, condition, thenStatement, elseStatement);
    }

    public Statement parseWhileStatement(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        Expression condition;
        try {
            expect(WHILE, ank);
            expect(L_PAREN, ank);
            condition = parseExpression(ank.fork(R_PAREN));
            expect(R_PAREN, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        Statement loop = parseStatement(ank);
        return new WhileStatement(pos, condition, loop);
    }

    public Statement parseReturnStatement(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        try {
            expect(RETURN, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        Expression returnExpression = new VoidExpression(pos);
        if (wlexer.peek() != SEMICOLON) {
            returnExpression = parseExpression(ank.fork(SEMICOLON));
        }
        SourcePosition pos2 = wlexer.position();
        try {
            expect(SEMICOLON, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos2);
        }
        return new ReturnStatement(pos, returnExpression);
    }

    public Statement parseExpressionStatement(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        Expression expression = parseExpression(ank.fork(SEMICOLON));
        SourcePosition pos2 = wlexer.position();
        try {
            expect(SEMICOLON, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos2);
        }
        return new ExpressionStatement(pos, expression);
    }

    public Expression parseExpression(AnchorSet ank) {
        return precedenceParser.parseExpression(ank);
    }

    public Expression parseUnaryExpression(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        IToken token = wlexer.peek();
        if (isPrimary(token)) {
            return parsePostfixExpression(ank);
        }
        if (token instanceof OperatorToken t) {
            switch (t) {
                case NOT -> {
                    wlexer.nextToken();
                    return new LogicalNotExpression(pos, parseUnaryExpression(ank));
                }
                case MINUS -> {
                    wlexer.nextToken();
                    return new NegativeExpression(pos, parseUnaryExpression(ank));
                }
                case INCREMENT, DECREMENT -> {
                    logger.printParserError(ParserErrorIds.UNSUPPORTED_OPERATOR_TOKEN, "Operation '%s' is not supported".formatted(lexer.peek().type()), lexer, lexer.peek().position());
                    lexer.nextToken();
                    return new ErrorExpression(lexer.peek().position());
                }
            }
        }
        logger.printParserError(ParserErrorIds.EXPECTED_PRIMARY_EXPRESSION, "Expected Primary Expression, such as Variable, Constant or MethodInvocation, but got '%s'".formatted(lexer.peek().type()), lexer, pos);
        skipToAnker(ank);
        return new ErrorExpression(pos);
    }

    public Expression parsePostfixExpression(AnchorSet ank) {
        Expression expression = parsePrimaryExpression(ank);

        while (true) {
            if (wlexer.peek() instanceof Token t) {
                switch (t) {
                    case DOT -> {
                        if (wlexer.peek(2) instanceof Token t2 && t2 == L_PAREN) {
                            expression = parseMethodInvocation(ank, expression);
                        } else {
                            expression = parseFieldAccess(ank, expression);
                        }
                    }
                    case L_SQUARE_BRACKET -> expression = parseArrayAccess(ank, expression);
                    default -> {
                        return expression;
                    }
                }
            } else if (wlexer.peek() instanceof OperatorToken op) {
                switch (op) {
                    case INCREMENT, DECREMENT -> {
                        logger.printParserError(ParserErrorIds.UNSUPPORTED_OPERATOR_TOKEN, "Operation '%s' is not supported".formatted(lexer.peek().type()), lexer, lexer.peek().position());
                        lexer.nextToken();
                        return new ErrorExpression(lexer.peek().position());
                    }
                    default -> {
                        return expression;
                    }
                }
            } else return expression;
        }

    }

    public Expression parseMethodInvocation(AnchorSet ank, Expression expression) {
        SourcePosition pos = wlexer.position();
        IdentifierToken ident;
        Arguments arguments;
        try {
            expect(DOT, ank);
            ident = expectIdentifier(ank);
            expect(L_PAREN, ank);
            arguments = parseArguments(ank.fork(R_PAREN));
            expect(R_PAREN, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new MethodInvocationOnObject(pos, expression, ident.getIdentifier(), arguments);
    }

    public Arguments parseArguments(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        Arguments arguments = new Arguments(pos);
        IToken token = wlexer.peek();

        if (token == R_PAREN) return arguments;

        do {
            arguments.addArgument(parseExpression(ank.fork(COMMA, R_PAREN)));
            if (wlexer.peek() != R_PAREN) {
                SourcePosition pos2 = wlexer.position();
                try {
                    expect(COMMA, ank.fork(R_PAREN));
                } catch (ExpectedTokenError e) {
                    arguments.addArgument(new ErrorExpression(pos2));
                    return arguments;
                }
                if (wlexer.peek() == COMMA) {
                    logger.printParserError(ParserErrorIds.EXPECTED_ARGUMENT, "Argument expected after Token \",\"!", lexer, lexer.peek().position());
                }
            } else {
                break;
            }
        } while (true);
        return arguments;
    }

    public Expression parseFieldAccess(AnchorSet ank, Expression expression) {
        SourcePosition pos = wlexer.position();
        IdentifierToken ident;
        try {
            expect(DOT, ank);
            ident = expectIdentifier(ank);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new FieldAccess(pos, expression, ident.getIdentifier());
    }

    public Expression parseArrayAccess(AnchorSet ank, Expression expression) {
        SourcePosition pos = wlexer.position();
        Expression arrayPosition;
        try {
            expect(L_SQUARE_BRACKET, ank);
            arrayPosition = parseExpression(ank.fork(R_SQUARE_BRACKET));
            expect(R_SQUARE_BRACKET, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new ArrayAccess(pos, expression, arrayPosition);
    }

    private boolean isExpression(IToken token) {
        // accept ++ and -- so that error message is more precise
        return (token == NOT || token == MINUS || token == INCREMENT || token == DECREMENT || isPrimary(token));
    }

    private boolean isPrimary(IToken token) {
        if (token instanceof IntegerToken) return true;
        if (token instanceof IdentifierToken) return true;
        if (token instanceof Token t) {
            return switch (t) {
                case NULL, FALSE, TRUE, THIS, NEW, L_PAREN -> true;
                default -> false;
            };
        }
        //this should not be possible, because all instances of IToken are checked
        return false;
    }

    private boolean isType(IToken token) {
        if (token instanceof IdentifierToken) return true;
        if (token instanceof TypeToken t) {
            return switch (t) {
                case INT_TYPE, BOOLEAN_TYPE, VOID_TYPE -> true;
                default -> false;
            };
        }
        return false;
    }

    public Expression parsePrimaryExpression(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        Expression expression = new ErrorExpression(pos);
        IToken token = wlexer.peek();
        if (token instanceof IdentifierToken ident) {
            wlexer.nextToken();
            if (wlexer.peek() == L_PAREN) {
                SourcePosition posParen = wlexer.position();
                Arguments arguments = new Arguments(posParen);
                try {
                    expect(L_PAREN, ank);
                    arguments = parseArguments(ank.fork(R_PAREN));
                    posParen = wlexer.position();
                    expect(R_PAREN, ank);
                } catch (ExpectedTokenError e) {
                    arguments.addArgument(new ErrorExpression(posParen));
                }
                //we create a ThisValue out of nowhere, because methods only can be invoked on other objects or the own object(this)
                expression = new MethodInvocationOnObject(pos, new ThisValue(pos), ident.getIdentifier(), arguments);
            } else {
                expression = new Variable(pos, ident.getIdentifier());
            }
        } else if (token instanceof IntegerToken i) {
            wlexer.nextToken();
            expression = new IntegerValue(pos, i.getValue());
        } else if (token instanceof Token t) {
            switch (t) {
                case NULL -> {
                    wlexer.nextToken();
                    expression = new NullValue(pos);
                }
                case FALSE -> {
                    wlexer.nextToken();
                    expression = new BooleanValue(pos, false);
                }
                case TRUE -> {
                    wlexer.nextToken();
                    expression = new BooleanValue(pos, true);
                }
                case THIS -> {
                    wlexer.nextToken();
                    expression = new ThisValue(pos);
                }
                case L_PAREN -> {
                    try {
                        expect(L_PAREN, ank);
                        expression = parseExpression(ank.fork(R_PAREN));
                        expect(R_PAREN, ank);
                    } catch (ExpectedTokenError e) {
                        //do nothing errorExpression is already set
                    }
                }
                case NEW -> {
                    if (wlexer.peek(2) instanceof Token t2) {
                        if (t2 == L_PAREN) {
                            expression = parseNewObjectExpression(ank);
                        } else if (t2 == L_SQUARE_BRACKET) {
                            expression = parseNewArrayExpression(ank);
                        } else {
                            logger.printParserError(ParserErrorIds.EXPECTED_OBJECT_INSTANTIATION, "Expected '(' or '[', but found '%s".formatted(wlexer.peek(2)), lexer, lexer.peek(2).position());
                        }
                    }
                }
                default -> logger.printParserError(ParserErrorIds.EXPECTED_PRIMARY_TYPE, "Expected primary type, but found '%s".formatted(wlexer.peek(0)), lexer, lexer.peek().position());
            }
        }
        return expression;
    }

    public Expression parseNewArrayExpression(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        BasicType type;
        int dimension;
        Expression size;
        try {
            expect(NEW, ank);
            type = parseBasicType();
            dimension = 1;
            expect(L_SQUARE_BRACKET, ank);
            size = parseExpression(ank.fork(R_SQUARE_BRACKET));
            expect(R_SQUARE_BRACKET, ank);
            while (wlexer.peek() == L_SQUARE_BRACKET) {
                if (wlexer.peek(1) != R_SQUARE_BRACKET) {
                    break;
                }
                expect(L_SQUARE_BRACKET, ank);
                expect(R_SQUARE_BRACKET, ank);
                dimension++;
            }
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new NewArrayExpression(pos, type, size, dimension);
    }

    public Expression parseNewObjectExpression(AnchorSet ank) {
        SourcePosition pos = wlexer.position();
        SourcePosition typePos;
        IdentifierToken ident;
        try {
            expect(NEW, ank);
            typePos = wlexer.position();
            ident = expectIdentifier(ank);
            expect(L_PAREN, ank);
            expect(R_PAREN, ank);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new NewObjectExpression(pos, new CustomType(typePos, ident.getIdentifier()));
    }

}
