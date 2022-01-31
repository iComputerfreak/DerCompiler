package de.dercompiler.intermediate.generation;

import de.dercompiler.Function;
import de.dercompiler.Program;
import de.dercompiler.intermediate.CodeGenerationErrorIds;
import de.dercompiler.intermediate.operation.Operation;
import de.dercompiler.intermediate.operation.UnaryOperations.LabelOperation;
import de.dercompiler.intermediate.selection.Datatype;
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
    private static final String SEPERATOR = " ";
    private static final String COLON = ":";

    private static final String COMMENT_FUNC_START = "# -- Begin";
    private static final String COMMENT_FUNC_END   = "# -- End  ";

    private void writeLine(BufferedWriter bw, String... line) throws IOException {
        bw.write(String.join("", line));
        bw.newLine();
    }

    private void assemblerInit(BufferedWriter bw) throws IOException {
        writeLine(bw, TEXT);
    }

    private void createFunctionHead(BufferedWriter bw, Function func) throws IOException {
        writeLine(bw, COMMENT_FUNC_START, SEPERATOR, func.getName());
        writeLine(bw, SPACING, ALIGN);
        writeLine(bw, SPACING, GLOBL, SEPERATOR, func.getName());
        writeLine(bw, SPACING, TYPE, SEPERATOR, func.getName(), FUNCTION_TYPE);
        writeLine(bw, func.getName(), COLON);
    }

    private void createInstruction(BufferedWriter bw, Operation op) throws IOException {
        writeLine(bw, SPACING, op.getAtntSyntax());
    }

    private void createLabel(BufferedWriter bw, LabelOperation labelOp) throws IOException {
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
        writeLine(bw, SIZE, SEPERATOR, func.getName(), SIZE_MINUS, func.getName());
        writeLine(bw, COMMENT_FUNC_END, SEPERATOR, func.getName());
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
