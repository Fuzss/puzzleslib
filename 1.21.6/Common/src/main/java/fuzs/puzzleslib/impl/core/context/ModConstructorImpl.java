package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.impl.core.ModContext;
import org.apache.logging.log4j.util.Strings;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ModConstructorImpl<T> {

    void construct(String modId, T modConstructor);

    static <T> void construct(String modId, Supplier<T> modConstructorSupplier, Supplier<ModConstructorImpl<T>> modConstructorImplSupplier, Consumer<ModContext> modContextConsumer) {
        if (Strings.isBlank(modId)) throw new IllegalArgumentException("mod id is empty");
        // build first to force class being loaded for executing buildables
        T modConstructor = modConstructorSupplier.get();
        modContextConsumer.accept(ModContext.get(modId));
        modConstructorImplSupplier.get().construct(modId, modConstructor);
    }
}
