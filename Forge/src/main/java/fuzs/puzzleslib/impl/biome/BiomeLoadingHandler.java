package fuzs.puzzleslib.impl.biome;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import net.minecraft.core.Holder;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Predicate;

public class BiomeLoadingHandler {
    @SuppressWarnings("RedundantTypeArguments")
    private static final Map<BiomeModifier.Phase, BiomeLoadingPhase> BIOME_MODIFIER_PHASE_CONVERSIONS = Maps.<BiomeModifier.Phase, BiomeLoadingPhase>immutableEnumMap(new HashMap<>() {{
        this.put(BiomeModifier.Phase.ADD, BiomeLoadingPhase.ADDITIONS);
        this.put(BiomeModifier.Phase.REMOVE, BiomeLoadingPhase.REMOVALS);
        this.put(BiomeModifier.Phase.MODIFY, BiomeLoadingPhase.MODIFICATIONS);
        this.put(BiomeModifier.Phase.AFTER_EVERYTHING, BiomeLoadingPhase.POST_PROCESSING);
    }});

    public static void register(String modId, IEventBus modEventBus, Multimap<BiomeLoadingPhase, BiomeModificationData> biomeLoadingEntries) {
        DeferredRegister<BiomeModifier> biomeModifiersRegistry = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIERS, modId);
        DeferredRegister<Codec<? extends BiomeModifier>> biomeModifierSerializersRegitry = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, modId);
        biomeModifiersRegistry.register(modEventBus);
        biomeModifierSerializersRegitry.register(modEventBus);
        BiomeModifierImpl biomeModifier = new BiomeModifierImpl(biomeLoadingEntries);
        biomeModifierSerializersRegitry.register("biome_modifiers_codec", () -> biomeModifier.codec());
        biomeModifiersRegistry.register("biome_modifiers", () -> biomeModifier);
    }

    private static class BiomeModifierImpl implements BiomeModifier {
        private final Codec<? extends BiomeModifier> codec = Codec.unit(this);
        private final Multimap<BiomeLoadingPhase, BiomeLoadingHandler.BiomeModificationData> biomeLoadingEntries;

        public BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModificationData> biomeLoadingEntries) {
            this.biomeLoadingEntries = biomeLoadingEntries;
        }

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            BiomeLoadingPhase loadingPhase = BIOME_MODIFIER_PHASE_CONVERSIONS.get(phase);
            Collection<BiomeModificationData> modifications = this.biomeLoadingEntries.get(loadingPhase);
            if (!modifications.isEmpty()) {
                BiomeLoadingContext loadingContext = BiomeLoadingContextForge.create(biome);
                BiomeModificationContext modificationContext = getBiomeModificationContext(builder);
                for (BiomeModificationData modification : modifications) {
                    if (modification.selector().test(loadingContext)) {
                        modification.modifier().accept(modificationContext);
                    }
                }
            }
        }

        @Override
        public Codec<? extends BiomeModifier> codec() {
            return this.codec;
        }
    }

    private static BiomeModificationContext getBiomeModificationContext(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
        ClimateSettingsContextForge climateSettings = new ClimateSettingsContextForge(builder.getClimateSettings());
        SpecialEffectsContextForge specialEffects = new SpecialEffectsContextForge(builder.getSpecialEffects());
        GenerationSettingsContextForge generationSettings = GenerationSettingsContextForge.create(builder.getGenerationSettings());
        MobSpawnSettingsContextForge mobSpawnSettings = new MobSpawnSettingsContextForge(builder.getMobSpawnSettings());
        return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
    }

    public record BiomeModificationData(BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) {

    }
}
