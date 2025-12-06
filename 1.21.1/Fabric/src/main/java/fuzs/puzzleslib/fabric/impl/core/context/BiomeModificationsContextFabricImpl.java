package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.fabric.impl.biome.*;
import net.fabricmc.fabric.api.biome.v1.BiomeModification;
import net.fabricmc.fabric.api.biome.v1.BiomeModifications;
import net.fabricmc.fabric.api.biome.v1.BiomeSelectionContext;
import net.fabricmc.fabric.api.biome.v1.ModificationPhase;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.level.biome.Biome;

import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class BiomeModificationsContextFabricImpl implements BiomeModificationsContext {
    private static final Map<BiomeLoadingPhase, ModificationPhase> BIOME_PHASE_CONVERSIONS = Maps.immutableEnumMap(
            ImmutableMap.of(BiomeLoadingPhase.ADDITIONS,
                    ModificationPhase.ADDITIONS,
                    BiomeLoadingPhase.REMOVALS,
                    ModificationPhase.REMOVALS,
                    BiomeLoadingPhase.MODIFICATIONS,
                    ModificationPhase.REPLACEMENTS,
                    BiomeLoadingPhase.POST_PROCESSING,
                    ModificationPhase.POST_PROCESSING));

    private final BiomeModification biomeModification;

    public BiomeModificationsContextFabricImpl(String modId) {
        this.biomeModification = BiomeModifications.create(ResourceLocation.fromNamespaceAndPath(modId,
                "biome_modifiers"));
    }

    @Override
    public void register(BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier) {
        this.registerBiomeModification(biomeLoadingPhase, biomeSelector, biomeModifier);
    }

    @Override
    public void registerBiomeModification(BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier) {
        Objects.requireNonNull(biomeLoadingPhase, "biome loading phase is null");
        Objects.requireNonNull(biomeSelector, "biome selector is null");
        Objects.requireNonNull(biomeModifier, "biome modifier is null");
        ModificationPhase modificationPhase = BIOME_PHASE_CONVERSIONS.get(biomeLoadingPhase);
        Objects.requireNonNull(modificationPhase, "modification phase is null");
        this.biomeModification.add(modificationPhase,
                (BiomeSelectionContext selectionContext) -> biomeSelector.test(new BiomeLoadingContextFabric(
                        selectionContext)),
                (BiomeSelectionContext selectionContext, net.fabricmc.fabric.api.biome.v1.BiomeModificationContext modificationContext) -> {
                    biomeModifier.accept(createModificationContext(modificationContext, selectionContext.getBiome()));
                });
    }

    private static BiomeModificationContext createModificationContext(net.fabricmc.fabric.api.biome.v1.BiomeModificationContext modificationContext, Biome biome) {
        ClimateSettingsContextFabric climateSettings = new ClimateSettingsContextFabric(biome,
                modificationContext.getWeather());
        SpecialEffectsContextFabric specialEffects = new SpecialEffectsContextFabric(biome.getSpecialEffects(),
                modificationContext.getEffects());
        GenerationSettingsContextFabric generationSettings = new GenerationSettingsContextFabric(biome.getGenerationSettings(),
                modificationContext.getGenerationSettings());
        MobSpawnSettingsContextFabric mobSpawnSettings = new MobSpawnSettingsContextFabric(biome.getMobSettings(),
                modificationContext.getSpawnSettings());
        return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
    }
}
