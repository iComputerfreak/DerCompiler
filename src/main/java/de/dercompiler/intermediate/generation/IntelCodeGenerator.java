package de.dercompiler.intermediate.generation;

import de.dercompiler.Function;
import de.dercompiler.Program;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.intermediate.operation.ConstantOperations.CommentOperation;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.LabelOperation;
import de.dercompiler.io.FileResolver;
import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;
import de.dercompiler.transformation.TargetTriple;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;

public class IntelCodeGenerator implements CodeGenerator {

    private static final String SPACING = "\t";
    private static final String SEPARATOR = " ";
    private static final String COLON = ":";

    private static final String COMMENT_FUNC_START = "# -- Begin";
    private static final String COMMENT_FUNC_END = "# -- End  ";
    private static final String COMMENT_SYMBOL = "#";

    private static final String TEXT = ".text";
    private static final String PREFIX = ".intel_syntax noprefix";
    private static final String GLOBL = ".globl";
    private static final String ALIGN = ".p2align    4, 0x90";
    private static final String SIZE = ".size";
    private static final String FUNC_END_LABEL = ".Lfunc_end";

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
        writeLine(bw, SPACING, PREFIX);
    }

    private void createFunctionHead(BufferedWriter bw, Function func) throws IOException {
        writeLine(bw, COMMENT_FUNC_START, SEPARATOR, func.getName());
        writeLine(bw, SPACING, GLOBL, SEPARATOR, func.getName());
        writeLine(bw, SPACING, ALIGN);
        writeLine(bw, func.getName(), COLON);
    }

    private void createInstruction(BufferedWriter bw, Operation op) throws IOException {
        if (op.getComment() != null)
            writeLineAndComment(bw, op.getComment(), SPACING, op.getIntelSyntax());
        else
            writeLine(bw, SPACING, op.getIntelSyntax());
    }

    private void createComment(BufferedWriter bw, CommentOperation comment) throws IOException {
        String com = comment.getIntelSyntax();
        for (String line : com.split("\n")) {
            writeLine(bw, SPACING, line);
        }
    }

    private void createLabel(BufferedWriter bw, LabelOperation labelOp) throws IOException {
        if (labelOp.getComment() != null)
            writeLineAndComment(bw, labelOp.getComment(), labelOp.getIntelSyntax());
        else
            writeLine(bw, labelOp.getIntelSyntax());
    }

    private void createInstructions(BufferedWriter bw, Function func) throws IOException {
        for (Operation operation : func.getOperations()) {
            if (operation instanceof LabelOperation lo) {
                createLabel(bw, lo);
            } else if (operation instanceof CommentOperation co) {
                createComment(bw, co);
            } else {
                createInstruction(bw, operation);
            }
        }
    }

    private void createFunctionFooter(BufferedWriter bw, Function func) throws IOException {
        writeLine(bw, COMMENT_FUNC_END, SEPARATOR, func.getName());
        if (!TargetTriple.isWindows()) {
            writeLine(bw, FUNC_END_LABEL, ("" + counter), COLON);
            writeLine(bw, SEPARATOR, SIZE, SEPARATOR, func.getName(), ", ", FUNC_END_LABEL, ("" + counter++), "-", func.getName());
        }
    }

    private int counter = 0;

    @Override
    public void createAssembler(Program program, String filename) {
        counter = 0;
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
