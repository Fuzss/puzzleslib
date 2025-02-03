package fuzs.puzzleslib.api.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import fuzs.puzzleslib.api.init.v3.registry.ResourceKeyHelper;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.JukeboxSong;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.EquipmentAsset;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractDatapackRegistriesProvider extends RegistriesDatapackGenerator implements RegistriesDataProvider {
    private final CompletableFuture<HolderLookup.Provider> fullRegistries;

    public AbstractDatapackRegistriesProvider(DataProviderContext context) {
        this(context.getPackOutput(), context.getRegistries());
    }

    public AbstractDatapackRegistriesProvider(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, CompletableFuture.completedFuture(RegistryAccess.EMPTY));
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> patchedRegistries = RegistryPatchGenerator.createLookup(
                registries,
                Util.make(new RegistrySetBuilder(), (RegistrySetBuilder registrySetBuilder) -> {
                    this.addBootstrap(registrySetBuilder::add);
                }));
        this.registries = patchedRegistries.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        this.fullRegistries = patchedRegistries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
    }

    public abstract void addBootstrap(RegistryBoostrapConsumer consumer);

    @Override
    public CompletableFuture<HolderLookup.Provider> getRegistries() {
        return this.fullRegistries;
    }

    public static void registerEnchantment(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        context.register(resourceKey, builder.build(resourceKey.location()));
    }

    public static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> resourceKey) {
        context.register(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F));
    }

    public static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> resourceKey, DamageEffects damageEffects) {
        context.register(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F, damageEffects));
    }

    public static void registerTrimMaterial(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor) {
        registerTrimMaterial(context, resourceKey, ingredient, descriptionColor, Collections.emptyMap());
    }

    public static void registerTrimMaterial(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, Map<ResourceKey<EquipmentAsset>, String> overrideArmorAssets) {
        Component component = ResourceKeyHelper.getComponent(resourceKey)
                .withStyle(Style.EMPTY.withColor(descriptionColor));
        TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(),
                ingredient,
                component,
                overrideArmorAssets);
        context.register(resourceKey, trimMaterial);
    }

    public static void registerInstrument(BootstrapContext<Instrument> context, ResourceKey<Instrument> resourceKey, Holder<SoundEvent> soundEvent, float useDuration, float range) {
        context.register(resourceKey,
                new Instrument(soundEvent, useDuration, range, ResourceKeyHelper.getComponent(resourceKey)));
    }

    public static void registerJukeboxSong(BootstrapContext<JukeboxSong> context, ResourceKey<JukeboxSong> resourceKey, Holder<SoundEvent> soundEvent, float lengthInSeconds, int comparatorOutput) {
        context.register(resourceKey,
                new JukeboxSong(soundEvent,
                        ResourceKeyHelper.getComponent(resourceKey),
                        lengthInSeconds,
                        comparatorOutput));
    }

    @FunctionalInterface
    public interface RegistryBoostrapConsumer {

        <T> void add(ResourceKey<? extends Registry<T>> key, RegistrySetBuilder.RegistryBootstrap<T> bootstrap);
    }
}
