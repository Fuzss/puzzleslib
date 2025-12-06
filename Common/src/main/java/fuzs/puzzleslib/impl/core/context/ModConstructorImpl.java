package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.BaseModConstructor;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.core.ModContext;
import net.minecraft.resources.ResourceLocation;
import org.apache.commons.lang3.function.Consumers;

import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Supplier;

public interface ModConstructorImpl<T> {

    void construct(String modId, T modConstructor, Set<ContentRegistrationFlags> contentRegistrationFlags);

    static <T extends BaseModConstructor> void construct(ResourceLocation resourceLocation, Supplier<T> modConstructorSupplier, Supplier<ModConstructorImpl<T>> constructionSupplier) {
        construct(resourceLocation, modConstructorSupplier, constructionSupplier, Consumers.nop());
    }

    static <T extends BaseModConstructor> void construct(ResourceLocation resourceLocation, Supplier<T> modConstructorSupplier, Supplier<ModConstructorImpl<T>> constructionSupplier, Consumer<ModContext> runBeforeConstruction) {
        PuzzlesLib.LOGGER.info("Constructing components for {}", resourceLocation);
        // build first to force the class being loaded for executing buildable elements
        T modConstructor = modConstructorSupplier.get();
        ModContext modContext = ModContext.get(resourceLocation.getNamespace());
        runBeforeConstruction.accept(modContext);
        Set<ContentRegistrationFlags> contentRegistrationFlags = Set.of(modConstructor.getContentRegistrationFlags());
        constructionSupplier.get().construct(resourceLocation.getNamespace(), modConstructor, contentRegistrationFlags);
    }
}
