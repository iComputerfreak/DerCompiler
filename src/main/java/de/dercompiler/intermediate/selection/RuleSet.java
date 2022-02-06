package de.dercompiler.intermediate.selection;

import de.dercompiler.intermediate.selection.rules.*;
import firm.nodes.*;

import java.util.List;
import java.util.Map;
import java.util.function.Consumer;

import static java.util.Map.entry;

public final class RuleSet {
    
    private static final Map<Class<? extends Node>, List<? extends SubstitutionRule<?>>> rules = Map.ofEntries(
            entry(Add.class, List.<SubstitutionRule<Add>>of(new AddRule(), /*new IncLRule(), new IncRRule(),*/ new ArrayAccessRule(), new ArrayAccessShlLRule(),
                    new ArrayAccessShlRRule(), new ArrayConstantAccessRule())),
            entry(And.class, List.<SubstitutionRule<And>>of(new AndRule())),
            entry(Address.class, List.<SubstitutionRule<Address>>of(new AddressRule())),
            entry(Block.class, List.<SubstitutionRule<Block>>of()),
            entry(Call.class, List.<SubstitutionRule<Call>>of(new CallRule())),
            entry(Cmp.class, List.<SubstitutionRule<Cmp>>of(new CmpRule())),
            entry(Const.class, List.<SubstitutionRule<Const>>of(new ConstRule())),
            entry(Cond.class, List.<SubstitutionRule<Cond>>of(new CondRule())),
            entry(Conv.class, List.<SubstitutionRule<Conv>>of(new ConstConvRule(), new ConvRule())),
            entry(Div.class, List.<SubstitutionRule<Div>>of(new DivRule())),
            entry(End.class, List.<SubstitutionRule<End>>of(new EndRule())),
            entry(Eor.class, List.<SubstitutionRule<Eor>>of(new EorRule())),
            entry(Jmp.class, List.<SubstitutionRule<Jmp>>of(new JmpRule())),
            entry(Load.class, List.<SubstitutionRule<Load>>of(new LoadRule() /*, new ThomasArrayAccess()*/)),
            entry(Member.class, List.<SubstitutionRule<Member>>of(new MemberRule())),
            entry(Minus.class, List.<SubstitutionRule<Minus>>of(new MinusRule())),
            entry(Mod.class, List.<SubstitutionRule<Mod>>of(new ModRule())),
            entry(Mul.class, List.<SubstitutionRule<Mul>>of(new MulRule())),
            entry(Not.class, List.<SubstitutionRule<Not>>of(new NotRule())),
            entry(Or.class, List.<SubstitutionRule<Or>>of(new OrRule())),
            entry(Phi.class, List.<SubstitutionRule<Phi>>of(new PhiRule())),
            entry(Return.class, List.<SubstitutionRule<Return>>of(new ReturnRule())),
            entry(Proj.class, List.<SubstitutionRule<Proj>>of(new ProjRule(), new ProjLoadRule(), new ParamRule(), new CondJmpRule(), new NewRule(), new ProjDivRule())),
            entry(Shl.class, List.<SubstitutionRule<Shl>>of(new ShlRule())),
            entry(Shr.class, List.<SubstitutionRule<Shr>>of(new ShrRule())),
            entry(Shrs.class, List.<SubstitutionRule<Shrs>>of(new SarRule())),
            entry(Start.class, List.<SubstitutionRule<Start>>of(new StartRule())),
            entry(Store.class, List.<SubstitutionRule<Store>>of(new StoreRule(), new IncLMemberRule(), new IncRMemberRule()/*, new ResetRule()*/)),
            entry(Sub.class, List.<SubstitutionRule<Sub>>of(new SubRule())),
            entry(Unknown.class, List.<SubstitutionRule<Unknown>>of(new UnknownRule()))
    );

    public static <T extends Node> void forNodeClass(Class<T> nodeClass, Consumer<SubstitutionRule<T>> consumer) {
        // Nothing can go wrong here
        //noinspection unchecked
        List<SubstitutionRule<T>> substitutionRules = (List<SubstitutionRule<T>>) rules.getOrDefault(nodeClass, List.<SubstitutionRule<T>>of());
        substitutionRules.forEach(consumer);
    }

    public static boolean containsKey(Class<? extends Node> aClass) {
        return rules.containsKey(aClass);
    }
}
