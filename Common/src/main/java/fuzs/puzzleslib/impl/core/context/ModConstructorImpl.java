package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ModConstructorImpl<T> {

    void construct(String modId, T modConstructor);

    static <T> void construct(ResourceLocation resourceLocation, Supplier<T> modConstructorSupplier, Supplier<ModConstructorImpl<T>> modConstructorImplSupplier, Consumer<ModContext> modContextConsumer) {
        PuzzlesLib.LOGGER.info("Constructing components for {}", resourceLocation);
        // build first to force the class being loaded for executing buildable elements
        T modConstructor = modConstructorSupplier.get();
        modContextConsumer.accept(ModContext.get(resourceLocation.getNamespace()));
        modConstructorImplSupplier.get().construct(resourceLocation.getNamespace(), modConstructor);
    }
}
