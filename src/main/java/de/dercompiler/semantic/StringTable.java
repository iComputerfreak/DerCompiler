package de.dercompiler.semantic;

import java.util.HashMap;
import java.util.Map;

public class StringTable {

    private final Map<String, Symbol> map;
    
    public StringTable() {
        map = new HashMap<>();
    }

    public Symbol findOrInsert(String string) {
        if (map.containsKey(string)){
            return map.get(string);
        }

        Symbol symbol = new Symbol(string);
        map.put(string, symbol);
        return symbol;
    }

    public boolean contains(String string){
        return map.containsKey(string);
    }
}
