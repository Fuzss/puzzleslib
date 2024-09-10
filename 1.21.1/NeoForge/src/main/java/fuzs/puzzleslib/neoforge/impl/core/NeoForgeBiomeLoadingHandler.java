package fuzs.puzzleslib.neoforge.impl.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.puzzleslib.neoforge.impl.biome.*;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * Forge implementation based on <a href="https://github.com/teamfusion/rottencreatures/blob/1.19.2/forge/src/main/java/com/github/teamfusion/platform/common/worldgen/forge/BiomeManagerImpl.java">BiomeManager</a>
 * from <a href="https://github.com/teamfusion/rottencreatures">Rotten Creatures mod</a>.
 */
public class NeoForgeBiomeLoadingHandler {
    @SuppressWarnings("RedundantTypeArguments")
    private static final Map<BiomeModifier.Phase, BiomeLoadingPhase> BIOME_MODIFIER_PHASE_CONVERSIONS = Maps.<BiomeModifier.Phase, BiomeLoadingPhase>immutableEnumMap(new HashMap<>() {{
        this.put(BiomeModifier.Phase.ADD, BiomeLoadingPhase.ADDITIONS);
        this.put(BiomeModifier.Phase.REMOVE, BiomeLoadingPhase.REMOVALS);
        this.put(BiomeModifier.Phase.MODIFY, BiomeLoadingPhase.MODIFICATIONS);
        this.put(BiomeModifier.Phase.AFTER_EVERYTHING, BiomeLoadingPhase.POST_PROCESSING);
    }});
    private static final String BIOME_MODIFICATIONS_NAME_KEY = "biome_modifications";
    private static final String BIOME_MODIFIERS_DATA_KEY = NeoForgeRegistries.Keys.BIOME_MODIFIERS.location().toString().replace(":", "/");
    private static final Function<String, ResourceLocation> BIOME_MODIFICATIONS_FILE_KEY = (String id) -> {
        return ResourceLocationHelper.fromNamespaceAndPath(id, BIOME_MODIFIERS_DATA_KEY + "/" + id + ".json");
    };
    private static final Function<ResourceLocation, String> BIOME_MODIFICATIONS_FILE_CONTENTS = (ResourceLocation id) -> {
        return "{\"type\":\"" + id + "\"}";
    };

    public static void register(String modId, IEventBus modEventBus, Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications) {
        DeferredRegister<MapCodec<? extends BiomeModifier>> deferredRegister = DeferredRegister.create(NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, modId);
        deferredRegister.register(modEventBus);
        deferredRegister.register(BIOME_MODIFICATIONS_NAME_KEY, new BiomeModifierImpl(biomeModifications)::codec);
    }

    public static RepositorySource buildPack(String modId) {
        ResourceLocation resourceLocation = ResourceLocationHelper.fromNamespaceAndPath(modId, NeoForgeBiomeLoadingHandler.BIOME_MODIFICATIONS_NAME_KEY);
        return PackResourcesHelper.buildServerPack(resourceLocation, () -> new AbstractModPackResources() {

            @Override
            public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
                if (path.equals(NeoForgeBiomeLoadingHandler.BIOME_MODIFIERS_DATA_KEY)) {
                    resourceOutput.accept(NeoForgeBiomeLoadingHandler.BIOME_MODIFICATIONS_FILE_KEY.apply(modId), () -> {
                        return new ByteArrayInputStream(NeoForgeBiomeLoadingHandler.BIOME_MODIFICATIONS_FILE_CONTENTS.apply(resourceLocation).getBytes(StandardCharsets.UTF_8));
                    });
                }
            }
        }, false);
    }

    private record BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications,
                                     MapCodec<? extends BiomeModifier> codec) implements BiomeModifier {

        private BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications, @Nullable MapCodec<? extends BiomeModifier> codec) {
            this.biomeModifications = biomeModifications;
            this.codec = MapCodec.unit(this);
        }

        public BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications) {
            this(biomeModifications, null);
        }

        private static BiomeModificationContext createBuilderBackedContext(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            ClimateSettingsContextNeoForge climateSettings = new ClimateSettingsContextNeoForge(builder.getClimateSettings());
            SpecialEffectsContextNeoForge specialEffects = new SpecialEffectsContextNeoForge(builder.getSpecialEffects());
            GenerationSettingsContextNeoForge generationSettings = new GenerationSettingsContextNeoForge(builder.getGenerationSettings());
            MobSpawnSettingsContextNeoForge mobSpawnSettings = new MobSpawnSettingsContextNeoForge(builder.getMobSpawnSettings());
            return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
        }

        @Override
        public void modify(Holder<Biome> biome, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            // no equivalent for BEFORE_EVERYTHING exists on Fabric, so we don't use it
            // therefore result from map can be null
            BiomeLoadingPhase loadingPhase = BIOME_MODIFIER_PHASE_CONVERSIONS.get(phase);
            if (loadingPhase == null) return;
            Collection<BiomeModification> modifications = this.biomeModifications.get(loadingPhase);
            if (modifications.isEmpty()) return;
            BiomeLoadingContext filter = new BiomeLoadingContextNeoForge(biome);
            BiomeModificationContext context = createBuilderBackedContext(builder);
            for (BiomeModification modification : modifications) {
                modification.tryApply(filter, context);
            }
        }
    }

    public record BiomeModification(Predicate<BiomeLoadingContext> selector,
                                    Consumer<BiomeModificationContext> modifier) {

        public void tryApply(BiomeLoadingContext filter, BiomeModificationContext context) {
            if (this.selector().test(filter)) this.modifier().accept(context);
        }
    }
}
