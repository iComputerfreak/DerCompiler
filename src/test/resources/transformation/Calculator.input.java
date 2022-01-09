class Main {
	public static void main(String[] args) throws Exception {
		StringLiterals literals = new StringLiterals();
		literals.init();

		Parser parser = new Parser();
		parser.init();

		boolean exit = false;

		while (!exit) {
			System.out.flush();

			int result = parser.parseExpression();

			if (!parser.error) {
				literals.RESULT_COLON.print();
				System.out.println(result);
			} else {
				literals.ERROR_INVALID_SYNTAX.print();
				literals.NEWLINE.print();
			}

			System.out.flush();

			parser.reset();

			Token curToken = parser.currentToken;

			if (curToken.type == curToken.getEOFType()) {
				exit = true;
			}
		}
	}
}

class TextBuilder {
	public Text text;
	public int index;

	public TextBuilder init(int length) {
		this.text = new Text();
		text.init(length);
		return this;
	}

	public TextBuilder c(int ch) {
		this.text.data[this.index] = ch;
		this.index = this.index + 1;

		return this;
	}

	public Text finish() {
		return this.text;
	}
}

class Text {
	public int[] data;
	public int length;

	public void init(int length) {
		this.length = length;
		this.data = new int[length];
	}

	public void print() {
		int idx = 0;

		while (idx < this.length) {
			System.out.write(this.data[idx]);

			idx = idx + 1;
		}
	}
}

class StringLiterals {
	public Text ERROR_INVALID_SYNTAX;
	public Text RESULT_COLON;
	public Text NEWLINE;

	public void init() {
		ERROR_INVALID_SYNTAX = new TextBuilder().init(21)
			.c(69).c(114).c(114).c(111).c(114).c(58).c(32).c(105).c(110)
			.c(118).c(97).c(108).c(105).c(100).c(32).c(115).c(121).c(110)
			.c(116).c(97).c(120).finish();

		RESULT_COLON = new TextBuilder().init(8)
			.c(82).c(101).c(115).c(117).c(108).c(116).c(58).c(32).finish();

		NEWLINE = new TextBuilder().init(8).c(10).finish();
	}

}

class Token {
	public int type;
	public int data;

	public void initNumber(int value) {
		this.type = getNumberType();
		this.data = value;
	}

	public void initAdd() {
		this.type = getAddType();
	}

	public void initSub() {
		this.type = getSubType();
	}

	public void initMul() {
		this.type = getMulType();
	}

	public void initLParen() {
		this.type = getLParenType();
	}

	public void initRParen() {
		this.type = getRParenType();
	}

	public void initEOF() {
		this.type = getEOFType();
	}

	public void initNewLine() {
		this.type = getNewLineType();
	}

	public boolean equals(Token other) {
		if (other == null) {
			return false;
		} else if (this.type == getNumberType() && this.type == other.type) {
			return this.data == other.data;
		} else {
			return this.type == other.type;
		}
	}

	public int getNumberType() { return 1; }
	public int getAddType() { return 2; }
	public int getSubType() { return 3; }
	public int getMulType() { return 4; }
	public int getLParenType() { return 10; }
	public int getRParenType() { return 11; }
	public int getNewLineType() { return 50; }
	public int getEOFType() { return 100; }
}

class Lexer {

	public int currentByte;

	public void init() throws Exception {
		nextByte();
	}

	public Token nextToken(Token token) throws Exception {
		skipWhitespace();

		if (isDigit(currentByte)) {
			int value = lexNumber();
			token.initNumber(value);

		} else if (isPlus(currentByte)) {
			token.initAdd();
			nextByte();

		} else if (isMinus(currentByte)) {
			token.initSub();
			nextByte();

		} else if (isStar(currentByte)) {
			token.initMul();
			nextByte();

		} else if (isLParen(currentByte)) {
			token.initLParen();
			nextByte();

		} else if (isRParen(currentByte)) {
			token.initRParen();
			nextByte();

		} else if (isNewLine(currentByte)) {
			token.initNewLine();

		} else {
			token.initEOF();
		}

		return token;
	}

	public void nextByte() throws Exception {
		currentByte = System.in.read();
	}

	public int lexNumber() throws Exception {
		int value = 0;
		
		while (isDigit(currentByte)) {
			value = 10 * value;
			value = value + digitValue(currentByte);

			nextByte();
		}

		return value;
	}

	public void skipWhitespace() throws Exception {
		while (isWhitespace(currentByte)) {
			nextByte();
		}
	}

	public boolean isDigit(int i) {
		return 48 <= i && i < 58;
	}

	public int digitValue(int i) {
		return i - 48;
	}

	public boolean isPlus(int i) {
		return i == 43;
	}

	public boolean isMinus(int i) {
		return i == 45;
	}

	public boolean isStar(int i) {
		return i == 42;
	}

	public boolean isLParen(int i) {
		return i == 40;
	}

	public boolean isRParen(int i) {
		return i == 41;
	}

	public boolean isWhitespace(int i) {
		return i == 32 || i == 9;
	}

	public boolean isNewLine(int i) {
		return i == 10;
	}
}

class Parser {
	public Lexer lexer;
	public Token currentToken;

	public boolean error;

	public void init() throws Exception {
		this.currentToken = new Token();
		this.lexer = new Lexer();

		reset();
	}

	public void reset() throws Exception {
		this.error = false;
		this.lexer.init();
		nextToken();
	}

	public void nextToken() throws Exception {
		this.currentToken = this.lexer.nextToken(this.currentToken);
	}

	public int parseExpression() throws Exception {
		int result = parseTerm();

		while (currentToken.type == currentToken.getAddType()
			|| currentToken.type == currentToken.getSubType()) {
			int opType = currentToken.type;

			nextToken();

			int rhs = parseExpression();

			if (opType == currentToken.getAddType()) {
				result = result + rhs;
			} else {
				result = result - rhs;
			}
		}

		return result;
	}

	public int parseTerm() throws Exception {
		int result = parseFactor();

		while (currentToken.type == currentToken.getMulType()) {
			nextToken();

			int rhs = parseTerm();

			result = result * rhs;
		}

		return result;
	}

	public int parseFactor() throws Exception {
		int result = 0;

		if (currentToken.type == currentToken.getNumberType()) {
			result = currentToken.data;
			nextToken();

		} else if (currentToken.type == currentToken.getLParenType()) {
			nextToken();
			result = parseExpression();

			if (currentToken.type == currentToken.getRParenType()) {
				nextToken();
			} else {
				this.error = true;
			}
		} else if (currentToken.type == currentToken.getSubType()) {
			nextToken();
			result = parseFactor();

			return -result;

		} else {
			this.error = true;
		}

		return result;
	}
}
