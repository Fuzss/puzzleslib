package fuzs.puzzleslib.impl.core.context;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.biome.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record BiomeModificationsContextFabricImpl(
        BiomeModification biomeModification) implements BiomeModificationsContext {
    @SuppressWarnings("RedundantTypeArguments")
    private static final Map<BiomeLoadingPhase, ModificationPhase> BIOME_PHASE_CONVERSIONS = Maps.<BiomeLoadingPhase, ModificationPhase>immutableEnumMap(new HashMap<>() {{
        this.put(BiomeLoadingPhase.ADDITIONS, ModificationPhase.ADDITIONS);
        this.put(BiomeLoadingPhase.REMOVALS, ModificationPhase.REMOVALS);
        this.put(BiomeLoadingPhase.MODIFICATIONS, ModificationPhase.REPLACEMENTS);
        this.put(BiomeLoadingPhase.POST_PROCESSING, ModificationPhase.POST_PROCESSING);
    }});

    public BiomeModificationsContextFabricImpl(String modId) {
        this(BiomeModifications.create(new ResourceLocation(modId, "biome_modifiers")));
    }

    private static BiomeModificationContext getBiomeModificationContext(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext modificationContext, Biome biome) {
        ClimateSettingsContextFabric climateSettings = new ClimateSettingsContextFabric(biome, modificationContext.getWeather());
        SpecialEffectsContextFabric specialEffects = new SpecialEffectsContextFabric(biome.getSpecialEffects(), modificationContext.getEffects());
        GenerationSettingsContextFabric generationSettings = new GenerationSettingsContextFabric(biome.getGenerationSettings(), modificationContext.getGenerationSettings());
        MobSpawnSettingsContextFabric mobSpawnSettings = new MobSpawnSettingsContextFabric(biome.getMobSettings(), modificationContext.getSpawnSettings());
        return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
    }

    private static void registerBiomeModification(BiomeModification biomeModification, BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) {
        ModificationPhase modificationPhase = BIOME_PHASE_CONVERSIONS.get(phase);
        Objects.requireNonNull(modificationPhase, "modification phase is null");
        biomeModification.add(modificationPhase, selectionContext -> selector.test(BiomeLoadingContextFabric.create(selectionContext)), (selectionContext, modificationContext) -> {
            modifier.accept(getBiomeModificationContext(modificationContext, selectionContext.getBiome()));
        });
    }

    @Override
    public void register(BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) {
        registerBiomeModification(this.biomeModification, phase, selector, modifier);
    }
}
