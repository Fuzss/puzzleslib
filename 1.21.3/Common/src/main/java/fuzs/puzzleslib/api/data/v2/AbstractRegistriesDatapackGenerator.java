package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.CaseFormat;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.api.data.v2.core.RegistriesDataProvider;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.equipment.trim.TrimMaterial;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

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
                new RegistrySetBuilder().add(registryKey, AbstractRegistriesDatapackGenerator.this::addBootstrap)
        );
        this.registries = patchedRegistries.thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        this.fullRegistries = patchedRegistries.thenApply(RegistrySetBuilder.PatchedRegistries::full);
        this.registryKey = registryKey;
    }

    public abstract void addBootstrap(BootstrapContext<T> context);

    @Override
    public String getName() {
        return getNameFromRegistryPath(this.registryKey.location().getPath()) + " Registry";
    }

    static String getNameFromRegistryPath(String registryPath) {
        String registryName = registryPath.replaceAll("\\W", "_");
        registryName = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(registryName);
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(registryName), ' ');
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
        registerTrimMaterial(context, resourceKey, ingredient, descriptionColor, itemModelIndex,
                Collections.emptyMap()
        );
    }

    protected static void registerTrimMaterial(BootstrapContext<TrimMaterial> context, ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex, Map<ResourceLocation, String> overrideArmorMaterials) {
        Component component = Component.translatable(Util.makeDescriptionId("trim_material", resourceKey.location()))
                .withStyle(Style.EMPTY.withColor(descriptionColor));
        TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(), ingredient, itemModelIndex,
                component, overrideArmorMaterials
        );
        context.register(resourceKey, trimMaterial);
    }
}
