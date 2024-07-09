package fuzs.puzzleslib.neoforge.api.data.v2;

import com.google.common.base.CaseFormat;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.Util;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstrapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.packs.PackType;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ArmorMaterial;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraft.world.item.enchantment.Enchantment;
import net.neoforged.neoforge.common.data.DatapackBuiltinEntriesProvider;
import net.neoforged.neoforge.common.data.ExistingFileHelper;
import org.apache.commons.lang3.StringUtils;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractBuiltInDataProvider<T> implements DataProvider {
    private final PackOutput output;
    private final String modId;
    private final CompletableFuture<HolderLookup.Provider> registries;

    private final ResourceKey<? extends Registry<T>> registryKey;
    private final ExistingFileHelper fileHelper;
    private final ExistingFileHelper.ResourceType resourceType;
    private BootstrapContext<T> bootstrapContext;

    public AbstractBuiltInDataProvider(ResourceKey<? extends Registry<T>> registryKey, NeoForgeDataProviderContext context) {
        this(registryKey, context.getModId(), context.getPackOutput(), context.getRegistries(), context.getFileHelper());
    }

    public AbstractBuiltInDataProvider(ResourceKey<? extends Registry<T>> registryKey, String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> registries, ExistingFileHelper fileHelper) {
        this.registryKey = registryKey;
        this.output = output;
        this.modId = modId;
        this.registries = registries;
        this.fileHelper = fileHelper;
        this.resourceType = new ExistingFileHelper.ResourceType(PackType.SERVER_DATA, ".json", registryKey.location().getPath());
    }

    protected final void add(ResourceKey<T> key, T value) {
        this.fileHelper.trackGenerated(key.location(), this.resourceType);
        this.bootstrapContext.register(key, value);
    }

    protected abstract void addBootstrap(BootstrapContext<T> context);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return new DatapackBuiltinEntriesProvider(this.output, this.registries, new RegistrySetBuilder().add(this.registryKey, (BootstrapContext<T> context) -> {
            this.bootstrapContext = context;
            this.addBootstrap(context);
        }), Collections.singleton(this.modId)).run(output);
    }

    @Override
    public String getName() {
        String name = this.registryKey.location().getPath().replaceAll("\\W", "_");
        name = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(name);
        name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), ' ');
        return name + " Built In Data";
    }

    public static abstract class Enchantments extends AbstractBuiltInDataProvider<Enchantment> {

        public Enchantments(NeoForgeDataProviderContext context) {
            super(Registries.ENCHANTMENT, context);
        }

        protected static void register(BootstrapContext<Enchantment> context, ResourceKey<Enchantment> key, Enchantment.Builder builder) {
            context.register(key, builder.build(key.location()));
        }
    }

    public static abstract class DamageTypes extends AbstractBuiltInDataProvider<DamageType> {

        public DamageTypes(NeoForgeDataProviderContext context) {
            super(Registries.DAMAGE_TYPE, context);
        }

        public DamageTypes(String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
            super(Registries.DAMAGE_TYPE, modId, output, lookupProvider, fileHelper);
        }

        protected void add(ResourceKey<DamageType> resourceKey) {
            this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F));
        }

        protected void add(ResourceKey<DamageType> resourceKey, DamageEffects damageEffects) {
            this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F, damageEffects));
        }
    }

    public static abstract class TrimMaterials extends AbstractBuiltInDataProvider<TrimMaterial> {

        public TrimMaterials(NeoForgeDataProviderContext context) {
            super(Registries.TRIM_MATERIAL, context);
        }

        public TrimMaterials(String modId, PackOutput output, CompletableFuture<HolderLookup.Provider> lookupProvider, ExistingFileHelper fileHelper) {
            super(Registries.TRIM_MATERIAL, modId, output, lookupProvider, fileHelper);
        }

        protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex) {
            this.add(resourceKey, ingredient, descriptionColor, itemModelIndex, Collections.emptyMap());
        }

        protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex, Map<Holder<ArmorMaterial>, String> overrideArmorMaterials) {
            Component description = Component.translatable(Util.makeDescriptionId("trim_material", resourceKey.location())).withStyle(Style.EMPTY.withColor(descriptionColor));
            TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(), ingredient, itemModelIndex, description, overrideArmorMaterials);
            this.add(resourceKey, trimMaterial);
        }
    }
}
