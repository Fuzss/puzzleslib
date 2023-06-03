package fuzs.puzzleslib.impl.core.context;

import com.google.common.base.Preconditions;
import com.google.common.collect.HashMultimap;
import com.google.common.collect.Maps;
import com.google.common.collect.Multimap;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingContext;
import fuzs.puzzleslib.api.biome.v1.BiomeLoadingPhase;
import fuzs.puzzleslib.api.biome.v1.BiomeModificationContext;
import fuzs.puzzleslib.api.core.v1.ContentRegistrationFlags;
import fuzs.puzzleslib.api.core.v1.Proxy;
import fuzs.puzzleslib.api.core.v1.context.BiomeModificationsContext;
import fuzs.puzzleslib.impl.biome.*;
import net.minecraft.core.Holder;
import net.minecraft.core.Registry;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.level.biome.Biome;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.world.BiomeLoadingEvent;
import net.minecraftforge.eventbus.api.EventPriority;
import org.apache.commons.lang3.ArrayUtils;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Predicate;

public record BiomeModificationsContextForgeImpl(
        ContentRegistrationFlags[] contentRegistrations) implements BiomeModificationsContext {
    @SuppressWarnings("RedundantTypeArguments")
    private static final Map<BiomeLoadingPhase, EventPriority> BIOME_MODIFIER_PHASE_CONVERSIONS = Maps.<BiomeLoadingPhase, EventPriority>immutableEnumMap(new HashMap<>() {{
        this.put(BiomeLoadingPhase.ADDITIONS, EventPriority.HIGH);
        this.put(BiomeLoadingPhase.REMOVALS, EventPriority.NORMAL);
        this.put(BiomeLoadingPhase.MODIFICATIONS, EventPriority.LOW);
        this.put(BiomeLoadingPhase.POST_PROCESSING, EventPriority.LOW);
    }});

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
                        ResourceKey<Biome> resourceKey = ResourceKey.create(Registry.BIOME_REGISTRY, evt.getName());
                        Holder<Biome> holder = Proxy.INSTANCE.getGameServer().registryAccess().registryOrThrow(Registry.BIOME_REGISTRY).getHolderOrThrow(resourceKey);
                        BiomeLoadingContext filter = BiomeLoadingContextForge.create(holder);
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
        SpecialEffectsContextForge specialEffects = new SpecialEffectsContextForge(evt.getEffects());
        GenerationSettingsContextForge generationSettings = GenerationSettingsContextForge.create(evt.getGeneration());
        MobSpawnSettingsContextForge mobSpawnSettings = new MobSpawnSettingsContextForge(evt.getSpawns());
        return new BiomeModificationContext(climateSettings, specialEffects, generationSettings, mobSpawnSettings);
    }

    private record BiomeModification(Predicate<BiomeLoadingContext> selector,
                                    Consumer<BiomeModificationContext> modifier) {

        public void tryApply(BiomeLoadingContext filter, BiomeModificationContext context) {
            if (this.selector().test(filter)) this.modifier().accept(context);
        }
    }
}
