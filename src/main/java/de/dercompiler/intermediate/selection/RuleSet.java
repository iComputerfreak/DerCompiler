package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.selection.rules.*;
import firm.nodes.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Map.entry;

public final class RuleSet {
    
    private static final Map<Class<? extends Node>, List<? extends SubstitutionRule<?>>> rules = Map.ofEntries(
            entry(Add.class, List.<SubstitutionRule<Add>>of(new AddRule(), new IncLRule(), new IncRRule(), new ArrayAccessRule())),
            entry(And.class, List.<SubstitutionRule<And>>of(new AndRule())),
            entry(Cmp.class, List.<SubstitutionRule<Cmp>>of(new CmpRule())),
            entry(Const.class, List.<SubstitutionRule<Const>>of(new ConstRule())),
            entry(Conv.class, List.<SubstitutionRule<Conv>>of(new ConstConvRule(), new ConvRule())),
            entry(Eor.class, List.<SubstitutionRule<Eor>>of(new EorRule())),
            entry(Member.class, List.<SubstitutionRule<Member>>of(new MemberRule())),
            entry(Minus.class, List.<SubstitutionRule<Minus>>of(new MinusRule())),
            entry(Mul.class, List.<SubstitutionRule<Mul>>of(new MulRule())),
            entry(Not.class, List.<SubstitutionRule<Not>>of(new NotRule())),
            entry(Or.class, List.<SubstitutionRule<Or>>of(new OrRule())),
            entry(Phi.class, List.<SubstitutionRule<Phi>>of(new PhiRule())),
            entry(Proj.class, List.<SubstitutionRule<Proj>>of(new ProjRule(), new LoadRule())),
            entry(Shl.class, List.<SubstitutionRule<Shl>>of(new ShlRule())),
            entry(Shr.class, List.<SubstitutionRule<Shr>>of(new ShrRule())),
            entry(Shrs.class, List.<SubstitutionRule<Shrs>>of(new ShrsRule())),
            entry(Store.class, List.<SubstitutionRule<Store>>of(new StoreRule())),
            entry(Sub.class, List.<SubstitutionRule<Sub>>of(new SubRule()))
    );

    public static <T extends Node> void forNodeClass(Class<T> nodeClass, Consumer<SubstitutionRule<T>> consumer) {
        // Nothing can go wrong here
        List<SubstitutionRule<T>> substitutionRules = (List<SubstitutionRule<T>>) rules.get(nodeClass);
        substitutionRules.forEach(consumer);
    }

    public static boolean containsKey(Class<? extends Node> aClass) {
        return rules.containsKey(aClass);
    }
}
