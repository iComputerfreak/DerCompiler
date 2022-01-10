package de.dercompiler.intermediate.operation2.Operand;

public class Adress implements Operand{

    int offset = 0;
    int scale = 1;
    Register base, index = null;

    public Adress(int offset, Register base, Register index, int scale){
        this.offset = offset;
        this.base = base;
        this.index = index;
        this.scale = scale;
    }

    public Adress(Register base){
        this.base = base;
    }

    @Override
    public String getIdentifier() {
        if (index == null){
            return Integer.toString(offset) + "(" + base.getIdentifier() + ")";
        } else {
            return Integer.toString(offset) + "(" + base.getIdentifier() + "," + index.getIdentifier() + "," + Integer.toString(scale) + ")";
        }
    }
}
