package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.neoforge.impl.core.BiomeLoadingHandler;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record BiomeModificationsContextForgeImpl(
        Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeEntries,
        Set<ContentRegistrationFlags> availableFlags) implements BiomeModificationsContext {

    @Override
    public void register(BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) {
        if (this.availableFlags.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS)) {
            Objects.requireNonNull(phase, "phase is null");
            Objects.requireNonNull(selector, "selector is null");
            Objects.requireNonNull(modifier, "modifier is null");
            this.biomeEntries.put(phase, new BiomeLoadingHandler.BiomeModification(selector, modifier));
        } else {
            ContentRegistrationFlags.throwForFlag(ContentRegistrationFlags.BIOME_MODIFICATIONS);
        }
    }
}
