package de.dercompiler.intermediate.generation;

import de.dercompiler.Program;

import java.io.FileOutputStream;

public interface CodeGenerator {

    void createAssembler(Program program, String filename);
}
