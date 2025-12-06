package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.MapCodec;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import fuzs.puzzleslib.neoforge.impl.biome.*;
import net.minecraft.core.Holder;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.PackOutput;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.level.biome.Biome;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.common.data.JsonCodecProvider;
import net.neoforged.neoforge.common.world.BiomeModifier;
import net.neoforged.neoforge.common.world.ModifiableBiomeInfo;
import net.neoforged.neoforge.registries.DeferredRegister;
import net.neoforged.neoforge.registries.NeoForgeRegistries;
import net.neoforged.neoforge.server.ServerLifecycleHooks;

import java.util.Collection;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public final class BiomeModificationsContextNeoForgeImpl implements BiomeModificationsContext {
    private final Multimap<BiomeLoadingPhase, Map.Entry<Predicate<BiomeLoadingContext>, Consumer<BiomeModificationContext>>> biomeModifications = HashMultimap.create();
    private final String modId;
    private final IEventBus eventBus;

    public BiomeModificationsContextNeoForgeImpl(String modId, IEventBus eventBus) {
        this.modId = modId;
        this.eventBus = eventBus;
    }

    @Override
    public void registerBiomeModification(BiomeLoadingPhase biomeLoadingPhase, Predicate<BiomeLoadingContext> biomeSelector, Consumer<BiomeModificationContext> biomeModifier) {
        Objects.requireNonNull(biomeLoadingPhase, "biome loading phase is null");
        Objects.requireNonNull(biomeSelector, "biome selector is null");
        Objects.requireNonNull(biomeModifier, "biome modifier is null");
        if (this.biomeModifications.isEmpty()) {
            BiomeModifier biomeModifierImpl = new BiomeModifierImpl();
            DeferredRegister<MapCodec<? extends BiomeModifier>> deferredRegister = DeferredRegister.create(
                    NeoForgeRegistries.Keys.BIOME_MODIFIER_SERIALIZERS,
                    this.modId);
            deferredRegister.register(this.eventBus);
            Holder<MapCodec<? extends BiomeModifier>> holder = deferredRegister.register("biome_modifications",
                    biomeModifierImpl::codec);
            DataProviderHelper.registerDataProviders(this.modId, (NeoForgeDataProviderContext context) -> {
                return new JsonCodecProvider<>(context.getPackOutput(),
                        PackOutput.Target.DATA_PACK,
                        NeoForgeRegistries.Keys.BIOME_MODIFIERS.location().toString().replace(':', '/'),
                        BiomeModifier.DIRECT_CODEC,
                        context.getRegistries(),
                        context.getModId()) {
                    @Override
                    protected void gather() {
                        this.unconditional(holder.getKey().location(), biomeModifierImpl);
                    }
                };
            });
        }

        this.biomeModifications.put(biomeLoadingPhase, Map.entry(biomeSelector, biomeModifier));
    }

    /**
     * Originally somewhat inspired by <a
     * href="https://github.com/teamfusion/rottencreatures/blob/1.19.2/forge/src/main/java/com/github/teamfusion/platform/common/worldgen/forge/BiomeManagerImpl.java">BiomeManager</a>
     * from <a href="https://github.com/teamfusion/rottencreatures">Rotten Creatures mod</a>.
     */
    private class BiomeModifierImpl implements BiomeModifier {
        private static final Map<Phase, BiomeLoadingPhase> BIOME_PHASE_CONVERSIONS = Maps.immutableEnumMap(ImmutableMap.of(
                Phase.ADD,
                BiomeLoadingPhase.ADDITIONS,
                Phase.REMOVE,
                BiomeLoadingPhase.REMOVALS,
                Phase.MODIFY,
                BiomeLoadingPhase.MODIFICATIONS,
                Phase.AFTER_EVERYTHING,
                BiomeLoadingPhase.POST_PROCESSING));

        private final MapCodec<? extends BiomeModifier> codec = MapCodec.unit(this);

        @Override
        public void modify(Holder<Biome> holder, Phase phase, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            // no equivalent for BEFORE_EVERYTHING exists on Fabric, so we don't use it;
            // therefore, it is possible for no mapping to be found
            BiomeLoadingPhase biomeLoadingPhase = BIOME_PHASE_CONVERSIONS.get(phase);
            if (biomeLoadingPhase != null) {
                Collection<Map.Entry<Predicate<BiomeLoadingContext>, Consumer<BiomeModificationContext>>> biomeModification = BiomeModificationsContextNeoForgeImpl.this.biomeModifications.get(
                        biomeLoadingPhase);
                if (!biomeModification.isEmpty()) {
                    MinecraftServer minecraftServer = ServerLifecycleHooks.getCurrentServer();
                    Objects.requireNonNull(minecraftServer, "minecraft server is null");
                    RegistryAccess registryAccess = minecraftServer.registryAccess();
                    BiomeLoadingContext biomeLoadingContext = new BiomeLoadingContextNeoForge(registryAccess, holder);
                    BiomeModificationContext biomeModificationContext = createModificationContext(registryAccess,
                            builder);
                    for (Map.Entry<Predicate<BiomeLoadingContext>, Consumer<BiomeModificationContext>> entry : biomeModification) {
                        if (entry.getKey().test(biomeLoadingContext)) {
                            entry.getValue().accept(biomeModificationContext);
                        }
                    }
                }
            }
        }

        private static BiomeModificationContext createModificationContext(RegistryAccess registryAccess, ModifiableBiomeInfo.BiomeInfo.Builder builder) {
            ClimateSettingsContextNeoForge climateSettings = new ClimateSettingsContextNeoForge(builder.getClimateSettings());
            SpecialEffectsContextNeoForge specialEffects = new SpecialEffectsContextNeoForge(builder.getSpecialEffects());
            GenerationSettingsContextNeoForge generationSettings = new GenerationSettingsContextNeoForge(registryAccess,
                    builder.getGenerationSettings());
            MobSpawnSettingsContextNeoForge mobSpawnSettings = new MobSpawnSettingsContextNeoForge(builder.getMobSpawnSettings());
            return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
        }

        @Override
        public MapCodec<? extends BiomeModifier> codec() {
            return this.codec;
        }
    }
}
