package fuzs.puzzleslib.impl.core;

import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import fuzs.puzzleslib.api.resources.v1.PackResourcesHelper;
import fuzs.puzzleslib.impl.biome.*;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.world.BiomeModifier;
import net.minecraftforge.common.world.ModifiableBiomeInfo;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import net.minecraftforge.registries.ForgeRegistries;
import org.apache.commons.lang3.StringUtils;
import org.jetbrains.annotations.Nullable;

import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * Forge implementation based on <a href="https://github.com/teamfusion/rottencreatures/blob/1.19.2/forge/src/main/java/com/github/teamfusion/platform/common/worldgen/forge/BiomeManagerImpl.java">BiomeManager</a>
 * from <a href="https://github.com/teamfusion/rottencreatures">Rotten Creatures mod</a>.
 */
public class BiomeLoadingHandler {
    @SuppressWarnings("RedundantTypeArguments")
    private static final Map<BiomeModifier.Phase, BiomeLoadingPhase> BIOME_MODIFIER_PHASE_CONVERSIONS = Maps.<BiomeModifier.Phase, BiomeLoadingPhase>immutableEnumMap(new HashMap<>() {{
        this.put(BiomeModifier.Phase.ADD, BiomeLoadingPhase.ADDITIONS);
        this.put(BiomeModifier.Phase.REMOVE, BiomeLoadingPhase.REMOVALS);
        this.put(BiomeModifier.Phase.MODIFY, BiomeLoadingPhase.MODIFICATIONS);
        this.put(BiomeModifier.Phase.AFTER_EVERYTHING, BiomeLoadingPhase.POST_PROCESSING);
    }});
    private static final String BIOME_MODIFICATIONS_NAME_KEY = "biome_modifications";
    private static final String BIOME_MODIFIERS_DATA_KEY = ForgeRegistries.Keys.BIOME_MODIFIERS.location().toString().replace(":", "/");
    private static final Function<String, ResourceLocation> BIOME_MODIFICATIONS_FILE_KEY = id -> new ResourceLocation(id, BIOME_MODIFIERS_DATA_KEY + "/" + id + ".json");
    private static final Function<ResourceLocation, String> BIOME_MODIFICATIONS_FILE_CONTENTS = id -> "{\"type\":\"" + id + "\"}";

    public static void register(String modId, IEventBus modEventBus, Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications) {
        DeferredRegister<Codec<? extends BiomeModifier>> deferredRegister = DeferredRegister.create(ForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS, modId);
        deferredRegister.register(modEventBus);
        deferredRegister.register(BIOME_MODIFICATIONS_NAME_KEY, new BiomeModifierImpl(biomeModifications)::codec);
    }

    public static RepositorySource buildPack(String modId) {
        ResourceLocation id = new ResourceLocation(modId, BiomeLoadingHandler.BIOME_MODIFICATIONS_NAME_KEY);
        String title = Stream.of(ContentRegistrationFlags.BIOME_MODIFICATIONS.name().toLowerCase(Locale.ROOT).split("_")).map(StringUtils::capitalize).collect(Collectors.joining(" "));
        return PackResourcesHelper.buildServerPack(() -> new AbstractModPackResources() {

            @Override
            public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
                if (path.equals(BiomeLoadingHandler.BIOME_MODIFIERS_DATA_KEY)) {
                    resourceOutput.accept(BiomeLoadingHandler.BIOME_MODIFICATIONS_FILE_KEY.apply(modId), () -> {
                        return new ByteArrayInputStream(BiomeLoadingHandler.BIOME_MODIFICATIONS_FILE_CONTENTS.apply(id).getBytes(StandardCharsets.UTF_8));
                    });
                }
            }
        }, id, Component.literal(title), CommonComponents.EMPTY, true, true, FeatureFlagSet.of(), true);
    }

    private record BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications,
                                     Codec<? extends BiomeModifier> codec) implements BiomeModifier {

        private BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications, @Nullable Codec<? extends BiomeModifier> codec) {
            this.biomeModifications = biomeModifications;
            this.codec = Codec.unit(this);
        }

        public BiomeModifierImpl(Multimap<BiomeLoadingPhase, BiomeModification> biomeModifications) {
            this(biomeModifications, null);
        }

        private static BiomeModificationContext createBuilderBackedContext(ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            ClimateSettingsContextForge climateSettings = new ClimateSettingsContextForge(builder.getClimateSettings());
            SpecialEffectsContextForge specialEffects = new SpecialEffectsContextForge(builder.getSpecialEffects());
            GenerationSettingsContextForge generationSettings = new GenerationSettingsContextForge(builder.getGenerationSettings());
            MobSpawnSettingsContextForge mobSpawnSettings = new MobSpawnSettingsContextForge(builder.getMobSpawnSettings());
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
            BiomeLoadingContext filter = new BiomeLoadingContextForge(biome);
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
