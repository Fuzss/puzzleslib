package fuzs.puzzleslib.api.data.v2;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import net.minecraft.Util;
import net.minecraft.core.*;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.MutableComponent;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Instrument;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.TrimMaterial;

import java.util.Collections;
import java.util.Map;
import java.util.StringJoiner;
import java.util.concurrent.CompletableFuture;

@Deprecated(forRemoval = true)
public abstract class AbstractRegistriesDatapackGenerator<T> extends RegistriesDatapackGenerator implements RegistriesDataProvider {
    private final CompletableFuture<HolderLookup.Provider> fullRegistries;
    private final ResourceKey<? extends Registry<T>> registryKey;

    public AbstractRegistriesDatapackGenerator(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getPackOutput(), context.getRegistries());
    }

    public AbstractRegistriesDatapackGenerator(ResourceKey<? extends Registry<T>> registryKey, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, CompletableFuture.completedFuture(RegistryAccess.EMPTY));
        CompletableFuture<RegistrySetBuilder.PatchedRegistries> patchedRegistries = RegistryPatchGenerator.createLookup(
                registries,
                new RegistrySetBuilder().add(registryKey, AbstractRegistriesDatapackGenerator.this::addBootstrap));
        this.registries = patchedRegistries.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        this.fullRegistries = patchedRegistries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
        this.registryKey = registryKey;
    }

    public abstract void addBootstrap(BootstrapContext<T> context);

    @Override
    public String getName() {
        return capitalizeFully(this.registryKey.location().getPath()) + " Registry";
    }

    @Override
    public CompletableFuture<HolderLookup.Provider> getRegistries() {
        return this.fullRegistries;
    }

    protected static void registerEnchantment(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
        context.register(resourceKey, builder.build(resourceKey.location()));
    }

    protected static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> resourceKey) {
        context.register(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F));
    }

    protected static void registerDamageType(BootstrapContext<DamageType> context, ResourceKey<DamageType> resourceKey, DamageEffects damageEffects) {
        context.register(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F, damageEffects));
    }

    protected static void registerTrimMaterial(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex) {
        registerTrimMaterial(context,
                resourceKey,
                ingredient,
                descriptionColor,
                itemModelIndex,
                Collections.emptyMap());
    }

    protected static void registerTrimMaterial(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex, Map<ResourceLocation, String> overrideArmorMaterials) {
        Component component = getComponent(resourceKey).withStyle(Style.EMPTY.withColor(descriptionColor));
        TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(),
                ingredient,
                itemModelIndex,
                component,
                overrideArmorMaterials);
        context.register(resourceKey, trimMaterial);
    }

    protected static void registerInstrument(BootstrapContext<Instrument> context, ResourceKey<Instrument> resourceKey, Holder<SoundEvent> soundEvent, float useDuration, float range) {
        context.register(resourceKey, new Instrument(soundEvent, useDuration, range, getComponent(resourceKey)));
    }

    public static String capitalizeFully(String s) {
        s = s.replaceAll("\\W+", " ").replace('_', ' ');
        StringJoiner stringJoiner = new StringJoiner(" ");
        for (String string : s.split("\\s+")) {
            if (!string.isEmpty()) {
                stringJoiner.add(Character.toUpperCase(string.charAt(0)) + string.substring(1));
            }
        }
        return stringJoiner.toString();
    }

    public static MutableComponent getComponent(ResourceKey<?> resourceKey) {
        return Component.translatable(getTranslationKey(resourceKey));
    }

    public static String getTranslationKey(ResourceKey<?> resourceKey) {
        return Util.makeDescriptionId(resourceKey.registry().getPath(), resourceKey.location());
    }

    public static MutableComponent getComponent(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return Component.translatable(getTranslationKey(registryKey, resourceLocation));
    }

    public static String getTranslationKey(ResourceKey<? extends Registry<?>> registryKey, ResourceLocation resourceLocation) {
        return Util.makeDescriptionId(Registries.elementsDirPath(registryKey), resourceLocation);
    }
}
