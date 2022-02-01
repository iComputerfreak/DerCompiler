package de.dercompiler.intermediate.generation;

import de.dercompiler.Function;
import de.dercompiler.Program;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.LabelOperation;
import de.dercompiler.io.FileResolver;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.io.*;

public class AtntCodeGenerator implements CodeGenerator {

    private static final String TEXT = ".text";

    private static final String ALIGN = ".p2align 4,,15";
    private static final String GLOBL = ".globl";
    private static final String TYPE = ".type";
    private static final String FUNCTION_TYPE = ", @function";

    private static final String SIZE = ".size";
    private static final String SIZE_MINUS = ", .-";

    private static final String SPACING = "\t";
    private static final String SEPARATOR = " ";
    private static final String COLON = ":";

    private static final String COMMENT_FUNC_START = "# -- Begin";
    private static final String COMMENT_FUNC_END = "# -- End  ";
    private static final String COMMENT_SYMBOL = "#";

    private static final int COMMENT_OFFSET = 40;

    private void writeLine(BufferedWriter bw, String... line) throws IOException {
        bw.write(String.join("", line));
        bw.newLine();
    }

    private void writeLineAndComment(BufferedWriter bw, String comment, String... line) throws IOException {
        String opCode = String.join("", line);
        int tabCount = (COMMENT_OFFSET - opCode.length()) / 4 + 1;
        tabCount = Math.max(tabCount, 1);
        writeLine(bw,
                opCode,
                SPACING.repeat(tabCount),
                COMMENT_SYMBOL,
                SEPARATOR,
                comment);
    }

    private void assemblerInit(BufferedWriter bw) throws IOException {
        writeLine(bw, SPACING, TEXT);
    }

    private void createFunctionHead(BufferedWriter bw, Function func) throws IOException {
        writeLine(bw, COMMENT_FUNC_START, SEPARATOR, func.getName());
        writeLine(bw, SPACING, ALIGN);
        writeLine(bw, SPACING, GLOBL, SEPARATOR, func.getName());
        writeLine(bw, SPACING, TYPE, SEPARATOR, func.getName(), FUNCTION_TYPE);
        writeLine(bw, func.getName(), COLON);
    }

    private void createInstruction(BufferedWriter bw, Operation op) throws IOException {
        if (op.getComment() != null)
            writeLineAndComment(bw, op.getComment(), SPACING, op.getAtntSyntax());
        else
            writeLine(bw, SPACING, op.getAtntSyntax());
    }

    private void createLabel(BufferedWriter bw, LabelOperation labelOp) throws IOException {
        if (labelOp.getComment() != null)
            writeLineAndComment(bw, labelOp.getComment(), labelOp.getAtntSyntax());
        else
            writeLine(bw, labelOp.getAtntSyntax());
    }

    private void createInstructions(BufferedWriter bw, Function func) throws IOException {
        for (Operation operation : func.getOperations()) {
            if (operation instanceof LabelOperation lo) {
                createLabel(bw, lo);
            } else {
                createInstruction(bw, operation);
            }
        }
    }

    private void createFunctionFooter(BufferedWriter bw, Function func) throws IOException {
        writeLine(bw, SIZE, SEPARATOR, func.getName(), SIZE_MINUS, func.getName());
        writeLine(bw, COMMENT_FUNC_END, SEPARATOR, func.getName());
    }

    @Override
    public void createAssembler(Program program, String filename) {

        FileResolver fileResolver = new FileResolver();
        File out = fileResolver.resolve(filename);
        try (FileWriter writer = new FileWriter(out); BufferedWriter bw = new BufferedWriter(writer)) {
            assemblerInit(bw);

            for (Function function : program.getFunctions()) {
                createFunctionHead(bw, function);
                createInstructions(bw, function);
                createFunctionFooter(bw, function);

                writeLine(bw);
            }

        } catch (IOException e) {
            new OutputMessageHandler(MessageOrigin.CODE_GENERATION).printErrorAndExit(CodeGenerationErrorIds.CANT_OUTPUT_FILE, "can't write file: " + out);
        }
    }
}
