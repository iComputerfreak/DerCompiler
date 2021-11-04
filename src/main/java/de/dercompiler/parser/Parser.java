package de.dercompiler.parser;

import de.dercompiler.ast.*;
import de.dercompiler.ast.expression.*;
import de.dercompiler.ast.statement.*;
import de.dercompiler.ast.type.*;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.lexer.Lexer;
import de.dercompiler.lexer.TokenOccurrence;
import de.dercompiler.lexer.token.IToken;
import de.dercompiler.lexer.token.IdentifierToken;
import de.dercompiler.lexer.token.IntegerToken;
import de.dercompiler.lexer.token.Token;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import static de.dercompiler.lexer.token.Token.*;

public class Parser {

    Lexer lexer;
    LexerWrapper wlexer;
    PrecedenceParser precedenceParser;
    private final OutputMessageHandler logger;

    public Parser(Lexer lexer) {
        this.lexer = lexer;
        this.wlexer = new LexerWrapper(lexer);
        this.precedenceParser = new PrecedenceParser(lexer, this);
        this.logger = new OutputMessageHandler(MessageOrigin.PARSER, System.err);
    }
    
    public Program parseProgram() {
        // ClassDeclaration*
        List<ClassDeclaration> classes = new ArrayList<>();
        while (lexer.peek().type() == CLASS) {
            classes.add(parseClassDeclaration());
        }
        if (lexer.nextToken().type() != EOF) {
            logger.printErrorAndExit(ParserErrorIds.EXPECTED_CLASS_DECLARATION, "Expected class declaration");
        }
        return new Program(classes);
    }
    
    public ClassDeclaration parseClassDeclaration() {
        // class IDENT { ClassMember* }
        expect(CLASS);
        IdentifierToken identifier = expectIdentifier();
        expect(L_CURLY_BRACKET);
        List<ClassMember> members = new ArrayList<>();
        // While our next token is not the '}' token
        while (lexer.peek().type() != R_CURLY_BRACKET) {
            members.add(parseClassMember());
        }
        // Consume the R_CURLY_BRACKET
        lexer.nextToken();
        return new ClassDeclaration(identifier.getIdentifier(), members);
    }
    
    public ClassMember parseClassMember() {
        // MainMethod ->    public static void IDENT ( Type IDENT )
        // Field ->         public Type IDENT ;
        // Method ->        public Type IDENT ( Parameters? ) MethodRest? Block
        if (lexer.peek().type() == PUBLIC) {
            // MainMethod
            if (lexer.peek(1).type() == STATIC) {
                return parseMainMethod();
            }
            // Check if a Type token follows
            IToken type = lexer.peek(1).type();
            if (type == INT_TYPE || type == BOOLEAN_TYPE || type == VOID || type instanceof IdentifierToken) {
                if (lexer.peek(2).type() instanceof IdentifierToken) {
                    // Field
                    if (lexer.peek(3).type() == SEMICOLON) {
                        return parseField();
                    }
                    // Method
                    if (lexer.peek(3).type() == L_PAREN) {
                        return parseMethod();
                    }
                }
            }
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_PUBLIC_KEYWORD, "Expected 'public' keyword");
        return null;
    }
    
    public Field parseField() {
        // public Type IDENT ;
        expect(PUBLIC);
        Type type = parseType();
        IdentifierToken fieldName = expectIdentifier();
        expect(SEMICOLON);
        return new Field(type, fieldName.getIdentifier());
    }
    
    public MainMethod parseMainMethod() {
        // public static void IDENT ( Type IDENT ) MethodRest? Block
        expect(PUBLIC);
        expect(STATIC);
        expect(VOID);
        IdentifierToken name = expectIdentifier();
        expect(L_PAREN);
        Type paramType = parseType();
        IdentifierToken paramName = expectIdentifier();
        expect(R_PAREN);
        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest();
        }
        BasicBlock block = parseBasicBlock();
        return new MainMethod(name.getIdentifier(), paramType, paramName.getIdentifier(), methodRest, block);
    }
    
    public Method parseMethod() {
        // public Type IDENT ( Parameters? ) MethodRest? Block
        expect(PUBLIC);
        Type type = parseType();
        IdentifierToken ident = expectIdentifier();
        expect(L_PAREN);
        // Check if we have parameters
        // First2(Parameters) = First2(Parameter) = First2(Type) u {IDENT} = First2(BasicType) x {IDENT}
        // = {int IDENT, boolean IDENT, void IDENT, IDENT IDENT}
        IToken t = lexer.peek().type();
        LinkedList<Parameter> params = new LinkedList<>();
        if (t == INT_TYPE || t == BOOLEAN_TYPE || t == VOID || t instanceof IdentifierToken) {
            if (lexer.peek(1).type() instanceof IdentifierToken) {
                params = parseParameters();
            }
        }
        expect(R_PAREN);
        MethodRest methodRest = null;
        if (lexer.peek().type() == THROWS) {
            methodRest = parseMethodRest();
        }
        BasicBlock block = parseBasicBlock();
        return new Method(type, ident.getIdentifier(), params, methodRest, block);
    }
    
    public MethodRest parseMethodRest() {
        // throws IDENT
        expect(THROWS);
        IdentifierToken ident = expectIdentifier();
        return new MethodRest(ident.getIdentifier());
    }

    public LinkedList<Parameter> parseParameters() {
        // Parameter ParametersRest
        LinkedList<Parameter> params = new LinkedList<>();
        params.addLast(parseParameter());
        return parseParametersRest(params);
    }

    public LinkedList<Parameter> parseParametersRest(LinkedList<Parameter> parameters) {
        // (, Parameter ParametersRest)?
        if (lexer.peek().type() == COMMA) {
            expect(COMMA);
            parameters.addLast(parseParameter());
            return parseParametersRest(parameters);
        }
        // If there is no rest, we return the parameters parsed until now
        return parameters;
    }

    public Parameter parseParameter() {
        // Type IDENT
        Type type = parseType();
        IdentifierToken ident = expectIdentifier();
        return new Parameter(type, ident.getIdentifier());
    }

    public Type parseType() {
        // BasicType TypeRest
        BasicType type = parseBasicType();
        int dimension = parseTypeRest();
        return new Type(type, dimension);
    }
    
    public int parseTypeRest() {
        // ([] TypeRest)?
        if (lexer.peek().type() == L_SQUARE_BRACKET) {
            expect(L_SQUARE_BRACKET);
            expect(R_SQUARE_BRACKET);
            return parseTypeRest() + 1;
        }
        // If there is no rest, we return null
        return 0;
    }
    
    public BasicType parseBasicType() {
        // int | boolean | void | IDENT
        IToken t = lexer.nextToken().type();
        if (t instanceof IdentifierToken ident) {
            return new CustomType(ident.getIdentifier());
        }
        if (t instanceof Token token) {
            switch (token) {
                case INT_TYPE:
                    return new IntType();
                case BOOLEAN_TYPE:
                    return new BooleanType();
                case VOID:
                    return new VoidType();
            }
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_BASIC_TYPE,
                "Expected 'int', 'boolean', 'void' or an identifier.");
        return null;
    }

    /**
     * Checks, if the lexer's next token matches with the given token and consumes it.
     * Otherwise, prints an error and exits the program.
     * @param t The token to check for.
     */
    private void expect(Token t) {
        // TODO: Read position and add it to the error output
        if (lexer.nextToken().type() != t) {
            logger.printErrorAndExit(ParserErrorIds.EXPECTED_TOKEN, "Expected " + t.toString() + ".");
        }
    }

    /**
     * Checks if the lexer's next token is an identifier.
     * @return The IdentifierToken, if there is one, otherwise the function prints an error and exits without returning anything.
     */
    private IdentifierToken expectIdentifier() {
        // TODO: Read position and add it to the error output
        TokenOccurrence t = lexer.nextToken();
        if (t.type() instanceof IdentifierToken) {
            return (IdentifierToken) t.type();
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_IDENTIFIER, "Identifier expected.");
        return null;
    }

    //since here we use wlexer instead of lexer

    public BasicBlock parseBasicBlock() {
        LinkedList<Statement> statements = new LinkedList<>();
        expect(L_CURLY_BRACKET);
        while(wlexer.peek() != R_CURLY_BRACKET) {
            statements.addLast(parseBlockStatement());
        }
        expect(R_CURLY_BRACKET);
        return new BasicBlock(statements);
    }

    public Statement parseBlockStatement() {
        IToken token = wlexer.peek();
        boolean possible_expression = isExpression(token);
        boolean possible_type = isType(token);
        //= token instanceof IdentifierToken
        if (possible_expression && possible_type) {
            //when ident[] varname -> variableDeclaration
            //when ident[expr] -> expression
            if (wlexer.peek(1) instanceof IdentifierToken || (wlexer.peek(1) == L_SQUARE_BRACKET && wlexer.peek(2) == R_SQUARE_BRACKET)) {
                return parseVariableDeclaration();
            } else {
                return parseStatement();
            }
        } else if (possible_type) {
            return parseVariableDeclaration();
        } else {
            //fuse statement possible_expression and non_primary because it is one call
            return parseStatement();
        }
    }

    public Statement parseVariableDeclaration() {
        Type type = parseType();
        IdentifierToken ident = expectIdentifier();
        AbstractExpression expression = new UninitializedValue();
        if (wlexer.peek() == ASSIGN) {
            expression = parseExpression();
        } else {
            expect(SEMICOLON);
        }
        return new LocalVariableDeclarationStatement(type, ident.getIdentifier(), expression);
    }

    public Statement parseStatement() {
        IToken token = wlexer.peek();
        if (token instanceof Token t) {
            return switch (t) {
                case L_CURLY_BRACKET -> parseBlockStatement();
                case SEMICOLON -> new EmptyStatement();
                case IF -> parseIfStatement();
                case WHILE -> parseWhileStatement();
                case RETURN -> parseReturnStatement();
                default -> parseExpressionStatement();
            };
        }
        return parseExpressionStatement();
    }

    public Statement parseIfStatement() {
        expect(IF);
        expect(L_PAREN);
        AbstractExpression condition = parseExpression();
        expect(R_PAREN);
        Statement thenStatement = parseStatement();
        Statement elseStatement = null;
        if (wlexer.peek() == ELSE) {
            expect(ELSE);
            elseStatement = parseStatement();
        }
        return new IfStatement(condition, thenStatement, elseStatement);
    }

    public Statement parseWhileStatement() {
        expect(WHILE);
        expect(L_PAREN);
        AbstractExpression condition = parseExpression();
        expect(R_PAREN);
        Statement loop = parseStatement();
        return new WhileStatement(condition, loop);
    }

    public Statement parseReturnStatement() {
        AbstractExpression returnExpression = new VoidExpression();
        if (wlexer.peek() != SEMICOLON)  {
            returnExpression = parseExpression();
        }
        expect(SEMICOLON);
        return new ReturnStatement(returnExpression);
    }

    public Statement parseExpressionStatement() {
        AbstractExpression expression = parseExpression();
        expect(SEMICOLON);
        return new ExpressionStatement(expression);
    }

    public AbstractExpression parseExpression() {
        return precedenceParser.parseExpression();
    }

    public AbstractExpression parseUnaryExpression() {
        IToken token = wlexer.peek();
        if (isPrimary(token)) {
            return parsePostfixExpression();
        }
        if (token instanceof Token t) {
            switch (t) {
                case NOT -> {
                    wlexer.nextToken();
                    return new LogicalNotExpression(parseUnaryExpression());
                }
                case MINUS -> {
                    wlexer.nextToken();
                    return new NegativeExpression(parseUnaryExpression());
                }
                case DECREMENT -> {
                    wlexer.nextToken();
                    return new NegativeExpression(new NegativeExpression(parseUnaryExpression()));
                }
            }
        }
        logger.printErrorAndExit(ParserErrorIds.EXPECTED_PRIMARY_EXPRESSION, "Expected Primary Expression, such as Variable, Constant or MethodInvocation!");
        return new ErrorExpression();
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
                case L_SQUARE_BRACKET -> {
                    expression = parseArrayAccess(expression);
                }
                default -> {
                    return expression;
                }
            }
        }
        return expression;
    }

    public AbstractExpression parseMethodInvocation(AbstractExpression expression) {
        expect(DOT);
        IdentifierToken ident = expectIdentifier();
        expect(L_PAREN);
        Arguments arguments = parseArguments();
        expect(R_PAREN);
        return new MethodInvocationOnObject(expression, ident.getIdentifier(), arguments);
    }

    public Arguments parseArguments() {
        Arguments arguments = new Arguments();
        IToken token = wlexer.peek();

        if (token == R_PAREN) return arguments;

        do {
            arguments.addArgument(parseExpression());
            if (wlexer.peek() != R_PAREN) {
                expect(COMMA);
                if (wlexer.peek() == COMMA) {
                    logger.printErrorAndExit(ParserErrorIds.EXPECTED_ARGUMENT, "Argument expect after Token \",\"!" + wlexer.position());
                }
            } else {
                break;
            }
        } while(true);
        return arguments;
    }

    public AbstractExpression parseFieldAccess(AbstractExpression expression) {
        expect(DOT);
        IdentifierToken ident = expectIdentifier();
        return new FieldAccess(expression, ident.getIdentifier());
    }

    public AbstractExpression parseArrayAccess(AbstractExpression expression) {
        expect(L_SQUARE_BRACKET);
        AbstractExpression arrayPosition = parseExpression();
        expect(R_SQUARE_BRACKET);
        return new ArrayAccess(expression, arrayPosition);
    }

    private boolean isExpression(IToken token) {
        if (token == NOT || token == MINUS) return true;
        return isPrimary(token);
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
        if (token instanceof  IdentifierToken) return true;
        if (token instanceof Token t) {
            return switch (t) {
                case INT_TYPE, BOOLEAN_TYPE, VOID -> true;
                default -> false;
            };
        }
        return false;
    }

    public AbstractExpression parsePrimaryExpression() {
        AbstractExpression expression = new ErrorExpression();
        IToken token = wlexer.peek();
        if (token instanceof IdentifierToken ident) {
            wlexer.nextToken();
            if (wlexer.peek() == L_PAREN) {
                expect(L_PAREN);
                Arguments arguments = parseArguments();
                expect(R_PAREN);
                //we create a ThisValue out of nowhere, because methods only can be invoked on other objects or the own object(this)
                expression = new MethodInvocationOnObject(new ThisValue(), ident.getIdentifier(), arguments);
            } else {
                expression = new Variable(ident.getIdentifier());
            }
        } else if (token instanceof IntegerToken i) {
            wlexer.nextToken();
            expression = new IntegerValue(i.getValue());
        } else if (token instanceof Token t) {
            switch(t) {
                case NULL: {
                    wlexer.nextToken();
                    expression = new NullValue();
                } break;
                case FALSE: {
                    wlexer.nextToken();
                    expression = new BooleanValue(false);
                } break;
                case TRUE: {
                    wlexer.nextToken();
                    expression = new BooleanValue(true);
                } break;
                case THIS: {
                    wlexer.nextToken();
                    expression = new ThisValue();
                } break;
                case L_PAREN: {
                    expect(L_PAREN);
                    expression = parseExpression();
                    expect(R_PAREN);
                } break;
                case NEW: {
                    if (wlexer.peek(2) instanceof Token t2) {
                        if (t2 == L_PAREN) {
                            expression = parseNewObjectExpression();
                        } else if (t2 == L_SQUARE_BRACKET) {
                            expression = parseNewArrayExpression();
                        } else {
                            logger.printErrorAndExit(ParserErrorIds.EXPECTED_OBJECT_INSTANTIATION, "Expected a object instantiation, in line " + wlexer.position() + "!");
                        }
                    }
                } break;
                default: {
                    logger.printErrorAndExit(ParserErrorIds.EXPECTED_PRIMARY_TYPE, "Expected primary-type, no primary type starts with token: " + wlexer.peek(0) + " in line: " + wlexer.position() + "!");
                }
            }
        }
        return expression;
    }

    public AbstractExpression parseNewArrayExpression() {
        expect(NEW);
        BasicType type = parseBasicType();
        int dimension =0;
        expect(L_SQUARE_BRACKET);
        AbstractExpression size = parseExpression();
        expect(R_SQUARE_BRACKET);
        while (wlexer.peek() == L_SQUARE_BRACKET) {
            expect(L_SQUARE_BRACKET);
            expect(R_SQUARE_BRACKET);
            dimension++;
        }
        return new NewArrayExpression(type, size, dimension);
    }

    public AbstractExpression parseNewObjectExpression() {
        expect(NEW);
        IdentifierToken ident = expectIdentifier();
        expect(L_PAREN);
        expect(R_PAREN);
        return new NewObjectExpression(new CustomType(ident.getIdentifier()));
    }

}
