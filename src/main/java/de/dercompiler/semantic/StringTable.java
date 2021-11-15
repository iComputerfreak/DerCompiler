package de.dercompiler.semantic;

import java.util.HashMap;
import java.util.Map;

public class StringTable {

    private final Map<String, Symbol> classesMap;
    private final Map<String, Symbol> methodsMap;
    private final Map<String, Symbol> variablesMap;
    
    public StringTable() {
        this.classesMap = new HashMap<>();
        this.methodsMap = new HashMap<>();
        this.variablesMap = new HashMap<>();
    }

    public Symbol findOrInsertClass(String string){
        return findOrInsertIn(classesMap, string);
    }

    public Symbol findOrInsertMethod(String string){
        return findOrInsertIn(methodsMap, string);
    }

    public Symbol findOrInsertVariable(String string){
        return findOrInsertIn(variablesMap, string);
    }
    
    private Symbol findOrInsertIn(Map<String, Symbol> map, String string) {
        if (map.containsKey(string)){
            return map.get(string);
        }

        Symbol symbol = new Symbol(string);
        map.put(string, symbol);
        return symbol;
    }

    public boolean containsClass(String string){
        return classesMap.containsKey(string);
    }

    public boolean containsMethod(String string){
        return methodsMap.containsKey(string);
    }

    public boolean containsVariable(String string){
        return variablesMap.containsKey(string);
    }

}
