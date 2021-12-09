package de.dercompiler.linker;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

public final class MSVC_CL implements Compiler, Linker {

    private String cl_path;

    public MSVC_CL(String tool) {
        cl_path = tool;
    }

    @Override
    public boolean checkCompiler() {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("not implemented yet.");
        return false;
    }

    @Override
    public void compile(CompilerCall call) {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("not implemented yet.");
    }

    @Override
    public boolean checkLinker() {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("not implemented yet.");
        return false;
    }

    @Override
    public void link(LinkerCall call) {
        new OutputMessageHandler(MessageOrigin.CODE_GENERATION).internalError("not implemented yet.");
    }
}
