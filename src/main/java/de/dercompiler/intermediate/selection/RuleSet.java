package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.selection.rules.*;

import java.util.List;

public final class RuleSet {
    
    private static final List<SubstitutionRule> rules = List.of(
            new AddRule(),
            new AndRule(),
            new ConstConvRule(),
            new EorRule(),
            new IncLRule(),
            new IncRRule(),
            new MinusRule(),
            new MulRule(),
            new NotRule(),
            new OrRule(),
            new ShlRule(),
            new ShrRule(),
            new ShrsRule(),
            new SubRule()
    );
    
    /**
     * Returns all rules that are to be used
     */
    public static List<SubstitutionRule> getRules() {
        return rules;
    }
}
