package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.data.v2.core.DataProviderContext;
import fuzs.puzzleslib.impl.core.context.FuelValuesContextImpl;
import fuzs.puzzleslib.neoforge.api.data.v2.core.DataProviderHelper;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.resources.ResourceKey;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.neoforged.neoforge.common.data.DataMapProvider;
import net.neoforged.neoforge.registries.datamaps.DataMapType;
import net.neoforged.neoforge.registries.datamaps.builtin.FurnaceFuel;
import net.neoforged.neoforge.registries.datamaps.builtin.NeoForgeDataMaps;

public final class FuelValuesContextNeoForgeImpl extends FuelValuesContextImpl {
    private final String modId;

    public FuelValuesContextNeoForgeImpl(String modId) {
        this.modId = modId;
    }

    @Override
    protected void registerListenerIfNecessary() {
        if (this.items.isEmpty() && this.itemTags.isEmpty()) {
            DataProviderHelper.registerDataProviders(this.modId, (DataProviderContext context) -> {
                return new DataMapProvider(context.getPackOutput(), context.getRegistries()) {
                    @Override
                    protected void gather(HolderLookup.Provider registries) {
                        Builder<FurnaceFuel, Item> builder = this.builder(NeoForgeDataMaps.FURNACE_FUELS);
                        for (Object2IntMap.Entry<Holder<? extends ItemLike>> entry : FuelValuesContextNeoForgeImpl.this.items.object2IntEntrySet()) {
                            Holder.Reference<Item> holder = entry.getKey().value().asItem().builtInRegistryHolder();
                            builder.add(holder, new FurnaceFuel(entry.getIntValue()), false);
                        }
                        for (Object2IntMap.Entry<TagKey<Item>> entry : FuelValuesContextNeoForgeImpl.this.itemTags.object2IntEntrySet()) {
                            builder.add(entry.getKey(), new FurnaceFuel(entry.getIntValue()), false);
                        }
                    }

                    @Override
                    public String getName() {
                        DataMapType<?, ?> dataMapType = NeoForgeDataMaps.FURNACE_FUELS;
                        return super.getName() + " for " +
                                ResourceKey.create(dataMapType.registryKey(), dataMapType.id());
                    }
                };
            });
        }
    }
}
