package de.dercompiler.pass;

import de.dercompiler.io.OutputMessageHandler;
import de.dercompiler.io.message.MessageOrigin;

import java.lang.reflect.InvocationTargetException;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class PassHelper {

    public static <K,V,Q extends K> List<V> transform(final List<Q> input, final java.util.function.Function<K,V> tfunc ) {
        if( null == input ) {
            return null;
        }
        return input.stream().map(tfunc).collect( Collectors.toList() );
    }

    public static Function<Class<AnalysisPass>, Pass> AnalysisUsageToPass = new Function<Class<AnalysisPass>, Pass>() {
        public Pass apply(Class<AnalysisPass> passClass) {
            try {
                return passClass.getConstructor().newInstance();
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
                new OutputMessageHandler(MessageOrigin.PASSES).internalError("cannot create Pass: " + passClass.getName() + ", it may hav no default constructor!", e);
                return null;
            }
        }
    };

    public static Function<Pass, Long> PassToPassID = new Function<Pass, Long>() {
        @Override
        public Long apply(Pass pass) {
            return pass.getID();
        }
    };
}
