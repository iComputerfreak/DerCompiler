package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.selection.rules.*;
import firm.nodes.*;

import java.util.List;
import java.util.Map;

import static java.util.Map.entry;

public final class RuleSet {
    
    private static final Map<Class<? extends Node>, List<SubstitutionRule>> rules = Map.ofEntries(
            entry(Add.class, List.of(new AddRule(), new IncLRule(), new IncRRule())),
            entry(And.class, List.of(new AndRule())),
            entry(Conv.class, List.of(new ConstConvRule())),
            entry(Eor.class, List.of(new EorRule())),
            entry(Minus.class, List.of(new MinusRule())),
            entry(Mul.class, List.of(new MulRule())),
            entry(Not.class, List.of(new NotRule())),
            entry(Or.class, List.of(new OrRule())),
            entry(Shl.class, List.of(new ShlRule())),
            entry(Shr.class, List.of(new ShrRule())),
            entry(Shrs.class, List.of(new ShrsRule())),
            entry(Sub.class, List.of(new SubRule()))
    );
    
    /**
     * Returns all rules that are to be used
     */
    public static Map<Class<? extends Node>, List<SubstitutionRule>> getRules() {
        return rules;
    }
}
