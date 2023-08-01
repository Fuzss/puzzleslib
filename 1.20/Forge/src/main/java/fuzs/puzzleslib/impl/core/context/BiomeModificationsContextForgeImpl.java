package fuzs.puzzleslib.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.core.BiomeLoadingHandler;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Objects;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record BiomeModificationsContextForgeImpl(
        Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModification> biomeEntries,
        Set<ContentRegistrationFlags> availableFlags) implements BiomeModificationsContext {

    @Override
    public void register(BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) {
        Preconditions.checkArgument(this.availableFlags.contains(ContentRegistrationFlags.BIOME_MODIFICATIONS), "biome modifications registration flag is missing");
        Objects.requireNonNull(phase, "phase is null");
        Objects.requireNonNull(selector, "selector is null");
        Objects.requireNonNull(modifier, "modifier is null");
        this.biomeEntries.put(phase, new BiomeLoadingHandler.BiomeModification(selector, modifier));
    }
}