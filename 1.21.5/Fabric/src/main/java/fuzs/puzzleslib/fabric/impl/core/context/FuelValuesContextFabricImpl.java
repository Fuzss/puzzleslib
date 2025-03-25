package fuzs.puzzleslib.fabric.impl.core.context;

import fuzs.puzzleslib.impl.core.context.FuelValuesContextImpl;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.fabricmc.fabric.api.registry.FuelRegistryEvents;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.entity.FuelValues;

public final class FuelValuesContextFabricImpl extends FuelValuesContextImpl {

    @Override
    protected void registerListenerIfNecessary() {
        if (this.items.isEmpty() && this.itemTags.isEmpty()) {
            FuelRegistryEvents.BUILD.register((FuelValues.Builder builder, FuelRegistryEvents.Context context) -> {
                for (Object2IntMap.Entry<Holder<? extends ItemLike>> entry : this.items.object2IntEntrySet()) {
                    builder.add(entry.getKey().value(),
                            entry.getIntValue() * this.fuelBaseValue() / context.baseSmeltTime());
                }
                for (Object2IntMap.Entry<TagKey<Item>> entry : this.itemTags.object2IntEntrySet()) {
                    builder.add(entry.getKey(), entry.getIntValue() * this.fuelBaseValue() / context.baseSmeltTime());
                }
            });
        }
    }
}
