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

public class Parser {

    Lexer lexer;
    LexerWrapper wlexer;
    PrecedenceParser precedenceParser;
    private final OutputMessageHandler logger;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.wlexer = new LexerWrapper(lexer);
        this.precedenceParser = new PrecedenceParser(lexer, this);
        this.logger = new OutputMessageHandler(MessageOrigin.PARSER);
    }
    
    public Program parseProgram() {
        // ClassDeclaration*
        SourcePosition pos = lexer.peek().position();
        List<ClassDeclaration> classes = new ArrayList<>();
        while (lexer.peek().type() == CLASS) {
            ClassDeclaration cd = parseClassDeclaration();
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
    
    public ClassDeclaration parseClassDeclaration() {
        // class IDENT { ClassMember* }
        SourcePosition pos = lexer.peek().position();
        IdentifierToken identifier;
        try {
            expect(CLASS);
            identifier = expectIdentifier();
            expect(L_CURLY_BRACKET);
        } catch (ExpectedTokenError e) {
            return null;
        }
        List<ClassMember> members = new ArrayList<>();
        // While our next token is not the '}' token
        while (lexer.peek().type() == PUBLIC) {
            members.add(parseClassMember());
        }
        // Consume the R_CURLY_BRACKET
        SourcePosition pos2 = lexer.peek().position();
        try {
            expect(R_CURLY_BRACKET);
        } catch (ExpectedTokenError e) {
            members.add(new ErrorClassMember(pos2));
        }
        return new ClassDeclaration(pos, identifier.getIdentifier(), members);
    }

    public ClassMember parseClassMember() {
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
            return parseMainMethod();
        }

        // Now we have to decide if we need to parse Field or Method
        // Since the Type can have unlimited tokens (due to array types), we need to parse the full type now
        // First we consume the public token that is still remaining
        Type type;
        IdentifierToken identifier;
        try {
            expect(PUBLIC);
            type = parseType();
            identifier = expectIdentifier();
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }
        // Now we decide which class member to parse
        // Field
        if (lexer.peek().type() == SEMICOLON) {
            return parseField(type, identifier);
        }
        // Method
        if (lexer.peek().type() == L_PAREN) {
            return parseMethod(type, identifier);
        }

        logger.printParserError(ParserErrorIds.EXPECTED_SEMICOLON, "Expected semicolon but found '%s'".formatted(lexer.peek(3).type()), lexer, lexer.peek(3).position());
        return new ErrorClassMember(pos);
    }
    
    public ClassMember parseField(Type type, IdentifierToken identifier) {
        // We already parsed "public Type IDENT"
        // public Type IDENT ;
        SourcePosition pos = lexer.peek().position();
        try {
            expect(SEMICOLON);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }
        return new Field(pos, type, identifier.getIdentifier());
    }
    
    public ClassMember parseMainMethod() {
        // public static void IDENT ( Type IDENT ) MethodRest? Block
        SourcePosition pos = lexer.peek().position();
        IdentifierToken name;
        Type paramType;
        IdentifierToken paramName;
        try {
            expect(PUBLIC);
            expect(STATIC);
            expect(VOID_TYPE);
            name = expectIdentifier();
            expect(L_PAREN);
            paramType = parseType();
            paramName = expectIdentifier();
            expect(R_PAREN);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }

        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest();
        }
        BasicBlock block = parseBasicBlock();
        return new MainMethod(pos, name.getIdentifier(), paramType, paramName.getIdentifier(), methodRest, block);
    }
    
    public ClassMember parseFullMethod() {
        SourcePosition pos = lexer.peek().position();
        Type type;
        IdentifierToken identifier;
        try {
            expect(PUBLIC);
            type = parseType();
            identifier = expectIdentifier();
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }

        return parseMethod(type, identifier);
    }
    
    public ClassMember parseMethod(Type type, IdentifierToken identifier) {
        // We already parsed "public Type IDENT"
        // public Type IDENT ( Parameters? ) MethodRest? Block\
        SourcePosition pos = lexer.peek().position();
        LinkedList<Parameter> params;
        try {
            expect(L_PAREN);
            // Check if we have parameters
            params = new LinkedList<>();
            // Parse the first argument
            if (lexer.peek().type() != R_PAREN) {
                params.add(parseParameter());
            }
            // If we have more arguments, they are each prefixed by a COMMA
            while (lexer.peek().type() == COMMA) {
                expect(COMMA);
                params.add(parseParameter());
            }
            expect(R_PAREN);
        } catch (ExpectedTokenError e) {
            return new ErrorClassMember(pos);
        }
        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest();
        }
        BasicBlock block = parseBasicBlock();
        return new Method(pos, type, identifier.getIdentifier(), params, methodRest, block);
    }

    public MethodRest parseMethodRest() {
        // throws IDENT
        SourcePosition pos;
        IdentifierToken ident;
        try {
            expect(THROWS);
            pos = lexer.peek().position();
            ident = expectIdentifier();
        } catch (ExpectedTokenError e) {
            //in this case null is okay, because it may also be null if it is not present
            return null;
        }
        return new MethodRest(pos, ident.getIdentifier());
    }

    public Parameter parseParameter() {
        // Type IDENT
        SourcePosition pos = lexer.peek().position();
        Type type = parseType();
        IdentifierToken ident;
        try {
            ident = expectIdentifier();
        } catch (ExpectedTokenError e) {
            return null;
        }
        return new Parameter(pos, type, ident.getIdentifier());
    }

    public Type parseType() {
        // BasicType TypeRest
        SourcePosition pos = lexer.peek().position();
        BasicType type = parseBasicType();
        int dimension = parseTypeRest();
        return new Type(pos, type, dimension);
    }
    
    public int parseTypeRest() {
        // ([] TypeRest)?
        if (lexer.peek().type() == L_SQUARE_BRACKET) {
            try {
                expect(L_SQUARE_BRACKET);
                expect(R_SQUARE_BRACKET);
            } catch (ExpectedTokenError e) {
                return -1;
            }
            return parseTypeRest() + 1;
        }
        // If there is no rest, we return null
        return 0;
    }
    
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
            }
        }

        logger.printParserError(ParserErrorIds.EXPECTED_BASIC_TYPE,
                "Expected 'int', 'boolean', 'void' or an identifier, but got '%s'".formatted(t), lexer, pos);
        return new ErrorType(pos);
    }

    /**
     * Checks, if the lexer's next token matches with the given token and consumes it.
     * Otherwise, prints an error and exits the program.
     *
     * @param expected The token to check for.
     */
    private void expect(IToken expected) throws ExpectedTokenError {
        TokenOccurrence t = lexer.nextToken();
        if (t.type() != expected) {
            logger.printParserError(ParserErrorIds.EXPECTED_TOKEN, "Expected %s but found '%s'".formatted(expected, t.type()), lexer, t.position());
            throw new ExpectedTokenError("Expected %s but found '%s'".formatted(expected, t.type()));
        }
    }

    /**
     * Checks if the lexer's next token is an identifier.
     *
     * @return The IdentifierToken, if there is one, otherwise the function prints an error and exits without returning anything.
     */
    private IdentifierToken expectIdentifier() throws ExpectedTokenError {
        TokenOccurrence t = lexer.nextToken();
        if (t.type() instanceof IdentifierToken it) {
            return it;
        }
        logger.printParserError(ParserErrorIds.EXPECTED_IDENTIFIER, "Identifier expected, but found '%s'".formatted(t.type().toString()), lexer, t.position());
        throw new ExpectedTokenError("Identifier expected, but found '%s'".formatted(t.type().toString()));
    }

    //since here we use wlexer instead of lexer

    private boolean isBasicBlock(IToken token) {
        return isType(token) || token == L_CURLY_BRACKET || token == SEMICOLON || token == IF || token == WHILE || isPrimary(token) || token == RETURN;
    }

    public BasicBlock parseBasicBlock() {
        SourcePosition pos = wlexer.position();
        LinkedList<Statement> statements = new LinkedList<>();
        try {
            expect(L_CURLY_BRACKET);
        } catch (ExpectedTokenError e) {
            statements.addLast(new ErrorStatement(pos));
            return new BasicBlock(pos, statements);
        }
        while (isBasicBlock(wlexer.peek())) {
            statements.addLast(parseBlockStatement());
        }
        SourcePosition pos2 = lexer.peek().position();
        try {
            expect(R_CURLY_BRACKET);
        } catch (ExpectedTokenError e) {
            //one off, but this is okay, because we never read it?!?
            statements.addLast(new ErrorStatement(pos2));
            return new BasicBlock(pos, statements);
        }
        return new BasicBlock(pos, statements);
    }

    public Statement parseBlockStatement() {
        IToken token = wlexer.peek();
        Statement statement;
        boolean possible_expression = isExpression(token);
        boolean possible_type = isType(token);
        //= token instanceof IdentifierToken
        if (possible_expression && possible_type) {
            //when ident[] varname -> variableDeclaration
            //when ident[expr] -> expression
            if (wlexer.peek(1) instanceof IdentifierToken || (wlexer.peek(1) == L_SQUARE_BRACKET && wlexer.peek(2) == R_SQUARE_BRACKET)) {
                statement = parseVariableDeclaration();
            } else {
                statement = parseStatement();
            }
        } else if (possible_type) {
            statement = parseVariableDeclaration();
        } else {
            //fuse statement possible_expression and non_primary because it is one call
            statement = parseStatement();
        }
        return statement;
    }

    public Statement parseVariableDeclaration() {
        SourcePosition pos = wlexer.position();
        Type type = parseType();
        IdentifierToken ident;
        AbstractExpression expression;
        try {
            ident = expectIdentifier();
            expression = new UninitializedValue(pos);
            if (wlexer.peek() == ASSIGN) {
                expect(ASSIGN);
                expression = parseExpression();
            }
            expect(SEMICOLON);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        return new LocalVariableDeclarationStatement(pos, type, ident.getIdentifier(), expression);
    }

    public Statement parseStatement() {
        SourcePosition pos = wlexer.position();
        IToken token = wlexer.peek();
        if (token instanceof Token t) {
            return switch (t) {
                case L_CURLY_BRACKET -> parseBasicBlock();
                case SEMICOLON -> wlexer.consumeToken(new EmptyStatement(pos));
                case IF -> parseIfStatement();
                case WHILE -> parseWhileStatement();
                case RETURN -> parseReturnStatement();
                default -> parseExpressionStatement();
            };
        }
        return parseExpressionStatement();
    }

    public Statement parseIfStatement() {
        SourcePosition pos = wlexer.position();
        AbstractExpression condition;
        Statement thenStatement;
        Statement elseStatement;
        try {
            expect(IF);
            expect(L_PAREN);
            condition = parseExpression();
            expect(R_PAREN);
            thenStatement = parseStatement();
            elseStatement = null;
            if (wlexer.peek() == ELSE) {
                expect(ELSE);
                elseStatement = parseStatement();
            }
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        return new IfStatement(pos, condition, thenStatement, elseStatement);
    }

    public Statement parseWhileStatement() {
        SourcePosition pos = wlexer.position();
        AbstractExpression condition;
        try {
            expect(WHILE);
            expect(L_PAREN);
            condition = parseExpression();
            expect(R_PAREN);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        Statement loop = parseStatement();
        return new WhileStatement(pos, condition, loop);
    }

    public Statement parseReturnStatement() {
        SourcePosition pos = wlexer.position();
        try {
            expect(RETURN);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos);
        }
        AbstractExpression returnExpression = new VoidExpression(pos);
        if (wlexer.peek() != SEMICOLON) {
            returnExpression = parseExpression();
        }
        SourcePosition pos2 = wlexer.position();
        try {
            expect(SEMICOLON);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos2);
        }
        return new ReturnStatement(pos, returnExpression);
    }

    public Statement parseExpressionStatement() {
        SourcePosition pos = wlexer.position();
        AbstractExpression expression = parseExpression();
        SourcePosition pos2 = wlexer.position();
        try {
            expect(SEMICOLON);
        } catch (ExpectedTokenError e) {
            return new ErrorStatement(pos2);
        }
        return new ExpressionStatement(pos, expression);
    }

    public AbstractExpression parseExpression() {
        return precedenceParser.parseExpression();
    }

    public AbstractExpression parseUnaryExpression() {
        SourcePosition pos = wlexer.position();
        IToken token = wlexer.peek();
        if (isPrimary(token)) {
            return parsePostfixExpression();
        }
        if (token instanceof OperatorToken t) {
            switch (t) {
                case NOT -> {
                    wlexer.nextToken();
                    return new LogicalNotExpression(pos, parseUnaryExpression());
                }
                case MINUS -> {
                    wlexer.nextToken();
                    return new NegativeExpression(pos, parseUnaryExpression());
                }
            }
        }
        logger.printParserError(ParserErrorIds.EXPECTED_PRIMARY_EXPRESSION, "Expected Primary Expression, such as Variable, Constant or MethodInvocation, but got '%s'".formatted(lexer.peek().type()), lexer, pos);
        return new ErrorExpression(pos);
    }

    public AbstractExpression parsePostfixExpression() {
        AbstractExpression expression = parsePrimaryExpression();

        while (wlexer.peek() instanceof Token t) {
            switch (t) {
                case DOT -> {
                    if (wlexer.peek(2) instanceof Token t2 && t2 == L_PAREN) {
                        expression = parseMethodInvocation(expression);
                    } else {
                        expression = parseFieldAccess(expression);
                    }
                }
                case L_SQUARE_BRACKET -> expression = parseArrayAccess(expression);
                default -> {
                    return expression;
                }
            }
        }
        return expression;
    }

    public AbstractExpression parseMethodInvocation(AbstractExpression expression) {
        SourcePosition pos = wlexer.position();
        IdentifierToken ident;
        Arguments arguments;
        try {
            expect(DOT);
            ident = expectIdentifier();
            expect(L_PAREN);
            arguments = parseArguments();
            expect(R_PAREN);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new MethodInvocationOnObject(pos, expression, ident.getIdentifier(), arguments);
    }

    public Arguments parseArguments() {
        SourcePosition pos = wlexer.position();
        Arguments arguments = new Arguments(pos);
        IToken token = wlexer.peek();

        if (token == R_PAREN) return arguments;

        do {
            arguments.addArgument(parseExpression());
            if (wlexer.peek() != R_PAREN) {
                SourcePosition pos2 = wlexer.position();
                try {
                    expect(COMMA);
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

    public AbstractExpression parseFieldAccess(AbstractExpression expression) {
        SourcePosition pos = wlexer.position();
        IdentifierToken ident;
        try {
            expect(DOT);
            ident = expectIdentifier();
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new FieldAccess(pos, expression, ident.getIdentifier());
    }

    public AbstractExpression parseArrayAccess(AbstractExpression expression) {
        SourcePosition pos = wlexer.position();
        AbstractExpression arrayPosition;
        try {
            expect(L_SQUARE_BRACKET);
            arrayPosition = parseExpression();
            expect(R_SQUARE_BRACKET);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new ArrayAccess(pos, expression, arrayPosition);
    }

    private boolean isExpression(IToken token) {
        return (token == NOT || token == MINUS || isPrimary(token));
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

    public AbstractExpression parsePrimaryExpression() {
        SourcePosition pos = wlexer.position();
        AbstractExpression expression = new ErrorExpression(pos);
        IToken token = wlexer.peek();
        if (token instanceof IdentifierToken ident) {
            wlexer.nextToken();
            if (wlexer.peek() == L_PAREN) {
                SourcePosition posParen = wlexer.position();
                Arguments arguments = new Arguments(posParen);
                try {
                    expect(L_PAREN);
                    arguments = parseArguments();
                    posParen = wlexer.position();
                    expect(R_PAREN);
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
                        expect(L_PAREN);
                        expression = parseExpression();
                        expect(R_PAREN);
                    } catch (ExpectedTokenError e) {
                        //do nothing errorExpression is already set
                    }
                }
                case NEW -> {
                    if (wlexer.peek(2) instanceof Token t2) {
                        if (t2 == L_PAREN) {
                            expression = parseNewObjectExpression();
                        } else if (t2 == L_SQUARE_BRACKET) {
                            expression = parseNewArrayExpression();
                        } else {
                            logger.printParserError(ParserErrorIds.EXPECTED_OBJECT_INSTANTIATION, "Expected an object instantiation", lexer, lexer.peek().position());
                        }
                    }
                }
                default -> logger.printParserError(ParserErrorIds.EXPECTED_PRIMARY_TYPE, "Expected primary type, no primary type starts with token: " + wlexer.peek(0), lexer, lexer.peek().position());
            }
        }
        return expression;
    }

    public AbstractExpression parseNewArrayExpression() {
        SourcePosition pos = wlexer.position();
        BasicType type;
        int dimension;
        AbstractExpression size;
        try {
            expect(NEW);
            type = parseBasicType();
            dimension = 1;
            expect(L_SQUARE_BRACKET);
            size = parseExpression();
            expect(R_SQUARE_BRACKET);
            while (wlexer.peek() == L_SQUARE_BRACKET) {
                expect(L_SQUARE_BRACKET);
                expect(R_SQUARE_BRACKET);
                dimension++;
            }
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new NewArrayExpression(pos, type, size, dimension);
    }

    public AbstractExpression parseNewObjectExpression() {
        SourcePosition pos = wlexer.position();
        SourcePosition typePos;
        IdentifierToken ident;
        try {
            expect(NEW);
            typePos = wlexer.position();
            ident = expectIdentifier();
            expect(L_PAREN);
            expect(R_PAREN);
        } catch (ExpectedTokenError e) {
            return new ErrorExpression(pos);
        }
        return new NewObjectExpression(pos, new CustomType(typePos, ident.getIdentifier()));
    }

}