package fuzs.puzzleslib.neoforge.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.CompostableBlocksContext;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import fuzs.puzzleslib.neoforge.api.data.v2.core.NeoForgeDataProviderContext;
import net.minecraft.core.Holder;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.builtin.Compostable;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

import java.util.IdentityHashMap;
import java.util.Map;
import java.util.Objects;

public record CompostableBlocksContextNeoForgeImpl(String modId,
                                                   Map<Holder<? extends ItemLike>, Compostable> compostingChances) implements CompostableBlocksContext {

    public CompostableBlocksContextNeoForgeImpl(String modId) {
        this(modId, new IdentityHashMap<>());
    }

    @Override
    public void registerCompostable(float compostingChance, Holder<? extends ItemLike>... items) {
        Preconditions.checkArgument(compostingChance >= 0.0F && compostingChance <= 1.0F,
                "Value " + compostingChance + " outside of range [" + 0.0F + ":" + 1.0F + "]");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        for (Holder<? extends ItemLike> item : items) {
            Objects.requireNonNull(item, "item is null");
            this.registerCompostable(item, compostingChance);
        }
    }

    private void registerCompostable(Holder<? extends ItemLike> item, float compostingChance) {
        if (this.compostingChances.isEmpty()) {
            DataProviderHelper.registerDataProviders(this.modId, (NeoForgeDataProviderContext context) -> {
                return new DataMapProvider(context.getPackOutput(), context.getRegistries()) {
                    @Override
                    protected void gather() {
                        Builder<Compostable, Item> builder = this.builder(NeoForgeDataMaps.COMPOSTABLES);
                        for (Map.Entry<Holder<? extends ItemLike>, Compostable> entry : CompostableBlocksContextNeoForgeImpl.this.compostingChances()
                                .entrySet()) {
                            Holder.Reference<Item> holder = entry.getKey().value().asItem().builtInRegistryHolder();
                            builder.add(holder, entry.getValue(), false);
                        }
                    }

                    @Override
                    public String getName() {
                        DataMapType<?, ?> dataMapType = NeoForgeDataMaps.COMPOSTABLES;
                        return super.getName() + " for " +
                                ResourceKey.create(dataMapType.registryKey(), dataMapType.id());
                    }
                };
            });
        }
        this.compostingChances.put(item, new Compostable(compostingChance));
    }
}
