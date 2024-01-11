package fuzs.puzzleslib.api.data.v1;

import com.google.common.base.CaseFormat;
import net.minecraft.Util;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.Registry;
import net.minecraft.core.RegistrySetBuilder;
import net.minecraft.core.registries.Registries;
import net.minecraft.data.CachedOutput;
import net.minecraft.data.DataProvider;
import net.minecraft.data.PackOutput;
import net.minecraft.data.worldgen.BootstapContext;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageEffects;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.item.ArmorMaterials;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.armortrim.TrimMaterial;
import net.minecraftforge.common.data.DatapackBuiltinEntriesProvider;
import net.minecraftforge.data.event.GatherDataEvent;
import org.apache.commons.lang3.StringUtils;

import java.util.Map;
import java.util.Set;
import java.util.concurrent.CompletableFuture;

public abstract class AbstractBuiltInDataProvider<T> implements DataProvider {
    private final PackOutput output;
    private final String modId;
    private final CompletableFuture<HolderLookup.Provider> lookupProvider;

    private final ResourceKey<? extends Registry<T>> registryKey;
    private BootstapContext<T> bootstapContext;

    public AbstractBuiltInDataProvider(ResourceKey<? extends Registry<T>> registryKey, GatherDataEvent evt, String modId) {
        this(registryKey, evt.getGenerator().getPackOutput(), modId, evt.getLookupProvider());
    }

    public AbstractBuiltInDataProvider(ResourceKey<? extends Registry<T>> registryKey, PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
        this.registryKey = registryKey;
        this.output = output;
        this.modId = modId;
        this.lookupProvider = lookupProvider;
    }

    protected final void add(ResourceKey<T> key, T value) {
        this.bootstapContext.register(key, value);
    }

    protected abstract void addBootstrap(BootstapContext<T> bootstapContext);

    @Override
    public CompletableFuture<?> run(CachedOutput output) {
        return new DatapackBuiltinEntriesProvider(this.output, this.lookupProvider, new RegistrySetBuilder().add(this.registryKey, context -> {
            this.bootstapContext = context;
            this.addBootstrap(context);
        }), Set.of(this.modId)).run(output);
    }

    @Override
    public String getName() {
        String name = this.registryKey.location().getPath().replaceAll("\\W", "_");
        name = CaseFormat.LOWER_UNDERSCORE.converterTo(CaseFormat.UPPER_CAMEL).convert(name);
        name = StringUtils.join(StringUtils.splitByCharacterTypeCamelCase(name), ' ');
        return name + " Built In Data";
    }

    public static abstract class DamageTypes extends AbstractBuiltInDataProvider<DamageType> {

        public DamageTypes(GatherDataEvent evt, String modId) {
            super(Registries.DAMAGE_TYPE, evt, modId);
        }

        public DamageTypes(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(Registries.DAMAGE_TYPE, output, modId, lookupProvider);
        }

        protected void add(ResourceKey<DamageType> resourceKey) {
            this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F));
        }

        protected void add(ResourceKey<DamageType> resourceKey, DamageEffects damageEffects) {
            this.add(resourceKey, new DamageType(resourceKey.location().getPath(), 0.1F, damageEffects));
        }
    }

    public static abstract class TrimMaterials extends AbstractBuiltInDataProvider<TrimMaterial> {

        public TrimMaterials(GatherDataEvent evt, String modId) {
            super(Registries.TRIM_MATERIAL, evt, modId);
        }

        public TrimMaterials(PackOutput output, String modId, CompletableFuture<HolderLookup.Provider> lookupProvider) {
            super(Registries.TRIM_MATERIAL, output, modId, lookupProvider);
        }

        protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex) {
            this.add(resourceKey, ingredient, descriptionColor, itemModelIndex, Map.of());
        }

        protected void add(ResourceKey<TrimMaterial> resourceKey, Item ingredient, int descriptionColor, float itemModelIndex, Map<ArmorMaterials, String> overrideArmorMaterials) {
            Component description = Component.translatable(Util.makeDescriptionId("trim_material", resourceKey.location())).withStyle(Style.EMPTY.withColor(descriptionColor));
            TrimMaterial trimMaterial = TrimMaterial.create(resourceKey.location().getPath(), ingredient, itemModelIndex, description, overrideArmorMaterials);
            this.add(resourceKey, trimMaterial);
        }
    }
}
