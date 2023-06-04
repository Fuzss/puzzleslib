package fuzs.puzzleslib.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.base.Suppliers;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import com.mojang.serialization.JsonOps;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.biome.*;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.data.BuiltinRegistries;
import net.minecraft.resources.RegistryOps;
import net.minecraft.resources.RegistryResourceAccess;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.dimension.DimensionType;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import net.minecraftforge.fml.util.ObfuscationReflectionHelper;
import org.apache.commons.lang3.ArrayUtils;

import java.lang.invoke.MethodHandles;
import java.lang.reflect.Method;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;
import java.util.function.Supplier;

public record BiomeModificationsContextForgeImpl(
        ContentRegistrationFlags[] contentRegistrations) implements BiomeModificationsContext {
    @SuppressWarnings("RedundantTypeArguments")
    private static final Map<BiomeLoadingPhase, EventPriority> BIOME_MODIFIER_PHASE_CONVERSIONS = Maps.<BiomeLoadingPhase, EventPriority>immutableEnumMap(new HashMap<>() {{
        this.put(BiomeLoadingPhase.ADDITIONS, EventPriority.HIGH);
        this.put(BiomeLoadingPhase.REMOVALS, EventPriority.NORMAL);
        this.put(BiomeLoadingPhase.MODIFICATIONS, EventPriority.LOW);
        this.put(BiomeLoadingPhase.POST_PROCESSING, EventPriority.LOW);
    }});
    private static final Supplier<RegistryAccess.Frozen> BUILT_IN_REGISTRY_ACCESS = Suppliers.memoize(() -> {
        return builtinCopy().freeze();
    });

    @Override
    public void register(BiomeLoadingPhase phase, Predicate<BiomeLoadingContext> selector, Consumer<BiomeModificationContext> modifier) {
        Preconditions.checkArgument(ArrayUtils.contains(this.contentRegistrations, ContentRegistrationFlags.BIOME_MODIFICATIONS), "biome modifications registration flag is missing");
        Objects.requireNonNull(phase, "phase is null");
        Objects.requireNonNull(selector, "selector is null");
        Objects.requireNonNull(modifier, "modifier is null");
        Multimap<BiomeLoadingPhase, BiomeModification> biomeEntries = HashMultimap.create();
        biomeEntries.put(phase, new BiomeModification(selector, modifier));
        this.registerBiomeModifications(biomeEntries);
    }

    private void registerBiomeModifications(Multimap<BiomeLoadingPhase, BiomeModification> biomeEntries) {
        for (Map.Entry<BiomeLoadingPhase, Collection<BiomeModification>> entry : biomeEntries.asMap().entrySet()) {
            if (!entry.getValue().isEmpty()) {
                EventPriority priority = BIOME_MODIFIER_PHASE_CONVERSIONS.get(entry.getKey());
                Objects.requireNonNull(priority, "priority is null");
                MinecraftForge.EVENT_BUS.addListener(priority, (final BiomeLoadingEvent evt) -> {
                    if (evt.getName() != null) {
                        BiomeLoadingContext filter = BiomeLoadingContextForge.create(BUILT_IN_REGISTRY_ACCESS.get(), ResourceKey.create(Registry.BIOME_REGISTRY, evt.getName()));
                        BiomeModificationContext context = createBuilderBackedContext(evt);
                        for (BiomeModification modification : entry.getValue()) {
                            modification.tryApply(filter, context);
                        }
                    }
                });
            }
        }
    }

    private static BiomeModificationContext createBuilderBackedContext(BiomeLoadingEvent evt) {
        ClimateSettingsContextForge climateSettings = new ClimateSettingsContextForge(evt::getClimate, evt::setClimate);
        SpecialEffectsContextForge specialEffects = new SpecialEffectsContextForge(evt::getEffects, evt::setEffects);
        GenerationSettingsContextForge generationSettings = new GenerationSettingsContextForge(BUILT_IN_REGISTRY_ACCESS.get(), evt.getGeneration());
        MobSpawnSettingsContextForge mobSpawnSettings = new MobSpawnSettingsContextForge(evt.getSpawns());
        return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
    }

    static RegistryAccess.Writable builtinCopy() {
        RegistryAccess.Writable writable = blankWriteable();
        RegistryResourceAccess.InMemoryStorage inMemoryStorage = new RegistryResourceAccess.InMemoryStorage();
        for (Map.Entry<ResourceKey<? extends Registry<?>>, RegistryAccess.RegistryData<?>> entry : RegistryAccess.REGISTRIES.entrySet()) {
            // filter out biomes as this is called when built-in biomes are constructed
            if (!entry.getKey().equals(Registry.DIMENSION_TYPE_REGISTRY) && !entry.getKey().equals(Registry.BIOME_REGISTRY)) {
                addBuiltinElements(inMemoryStorage, entry.getValue());
            }
        }

        RegistryOps.createAndLoad(JsonOps.INSTANCE, writable, inMemoryStorage);
        return DimensionType.registerBuiltin(writable);
    }

    private static RegistryAccess.Writable blankWriteable() {
        Method blankWriteable = ObfuscationReflectionHelper.findMethod(RegistryAccess.class, "m_206212_");
        blankWriteable.setAccessible(true);
        try {
            return (RegistryAccess.Writable) MethodHandles.lookup().unreflect(blankWriteable).invoke();
        } catch (Throwable e) {
            throw new RuntimeException(e);
        }
    }

    private static <E> void addBuiltinElements(RegistryResourceAccess.InMemoryStorage pDestinationRegistryHolder, RegistryAccess.RegistryData<E> pData) {
        ResourceKey<? extends Registry<E>> resourceKey = pData.key();
        Registry<E> registry = BuiltinRegistries.ACCESS.registryOrThrow(resourceKey);

        for (Map.Entry<ResourceKey<E>, E> entry : registry.entrySet()) {
            ResourceKey<E> otherResourceKey = entry.getKey();
            E e = entry.getValue();
            pDestinationRegistryHolder.add(BuiltinRegistries.ACCESS, otherResourceKey, pData.codec(), registry.getId(e), e, registry.lifecycle(e));
        }
    }

    private record BiomeModification(Predicate<BiomeLoadingContext> selector,
                                     Consumer<BiomeModificationContext> modifier) {

        public void tryApply(BiomeLoadingContext filter, BiomeModificationContext context) {
            if (this.selector().test(filter)) this.modifier().accept(context);
        }
    }
}
