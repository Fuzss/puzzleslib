package fuzs.puzzleslib.api.data.v2;

import com.google.common.base.CaseFormat;
import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.PackOutput;
import net.minecraft.data.registries.RegistriesDatapackGenerator;
import net.minecraft.data.registries.RegistryPatchGenerator;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractRegistriesDatapackGenerator<T> extends RegistriesDatapackGenerator {
    private final ResourceKey<? extends Registry<T>> registryKey;
    private BootstrapContext<T> bootstrapContext;

    public AbstractRegistriesDatapackGenerator(ResourceKey<? extends Registry<T>> registryKey, DataProviderContext context) {
        this(registryKey, context.getPackOutput(), context.getRegistries());
    }

    public AbstractRegistriesDatapackGenerator(ResourceKey<? extends Registry<T>> registryKey, PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
        super(output, registries);
        this.registryKey = registryKey;
    }

    protected final void add(ResourceKey<T> resourceKey, T value) {
        this.bootstrapContext.register(resourceKey, value);
    }

    protected abstract void addBootstrap(BootstrapContext<T> context);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        CompletableFuture<HolderLookup.Provider> registries = this.registries;
        return CompletableFuture.runAsync(() -> {
            this.registries = RegistryPatchGenerator.createLookup(registries,
                    new RegistrySetBuilder().add(this.registryKey, (BootstrapContext<T> context) -> {
                        this.bootstrapContext = context;
                        this.addBootstrap(context);
                    })
            ).thenApply(RegistrySetBuilder.PatchedRegistries::patches);
        }).thenCompose((Void $) -> {
            return super.run(output);
        }).thenRun(() -> {
            this.registries = registries;
        });
    }

    @Override
    public String getName() {
        return getNameFromRegistryPath(this.registryKey.location().getPath()) + " Registry";
    }

    static String getNameFromRegistryPath(String registryPath) {
        String registryName = registryPath.replaceAll("\\W", "_");
        registryName = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(registryName);
        return StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(registryName), ' ');
    }

    public static abstract class Enchantments extends AbstractRegistriesDatapackGenerator<Enchantment> {

        public Enchantments(DataProviderContext context) {
            super(Registries.ENCHANTMENT, context);
        }

        public Enchantments(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(Registries.ENCHANTMENT, output, registries);
        }

        protected void add(ResourceKey<Enchantment> resourceKey, Enchantment.Builder builder) {
            this.add(resourceKey, builder.build(resourceKey.location()));
        }
    }

    public static abstract class DamageTypes extends AbstractRegistriesDatapackGenerator<DamageType> {

        public DamageTypes(DataProviderContext context) {
            super(Registries.DAMAGE_TYPE, context);
        }

        public DamageTypes(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(Registries.DAMAGE_TYPE, output, registries);
        }

        protected void add(ResourceKey<DamageType> resourceKey) {
            this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F));
        }

        protected void add(ResourceKey<DamageType> resourceKey, DamageEffects damageEffects) {
            this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F, damageEffects));
        }
    }

    public static abstract class TrimMaterials extends AbstractRegistriesDatapackGenerator<TrimMaterial> {

        public TrimMaterials(DataProviderContext context) {
            super(Registries.TRIM_MATERIAL, context);
        }

        public TrimMaterials(PackOutput output, CompletableFuture<HolderLookup.Provider> registries) {
            super(Registries.TRIM_MATERIAL, output, registries);
        }

        protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex) {
            this.add(resourceKey, ingredient, descriptionColor, itemModelIndex, Collections.emptyMap());
        }

        protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex, Map<Holder<ArmorMaterial>, String> overrideArmorMaterials) {
            Component description = Component.translatable(
                    Util.makeDescriptionId("trim_material", resourceKey.location())).withStyle(
                    Style.EMPTY.withColor(descriptionColor));
            TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(), ingredient,
                    itemModelIndex, description, overrideArmorMaterials
            );
            this.add(resourceKey, trimMaterial);
        }
    }
}
