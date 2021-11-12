package de.dercompiler.semantic;

import de.dercompiler.lexer.token.IdentifierToken;

import java.util.HashMap;
import java.util.Map;

public class StringTable {

    private Map<String, Symbol> map;



    public StringTable(){
        map = new HashMap<String, Symbol>();
    }

    public Symbol findOrInsert(String string){
        if (map.containsKey(string)){
            return map.get(string);
        }

        Symbol symbol = new Symbol(string);
        map.put(string, symbol);
        return symbol;
    }

}
