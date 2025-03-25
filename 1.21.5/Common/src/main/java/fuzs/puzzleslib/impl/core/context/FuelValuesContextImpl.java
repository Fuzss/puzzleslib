package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.FuelValuesContext;
import it.unimi.dsi.fastutil.objects.Object2IntArrayMap;
import it.unimi.dsi.fastutil.objects.Object2IntMap;
import net.minecraft.core.Holder;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

public abstract class FuelValuesContextImpl implements FuelValuesContext {
    protected final Object2IntMap<Holder<? extends ItemLike>> items = new Object2IntArrayMap<>();
    protected final Object2IntMap<TagKey<Item>> itemTags = new Object2IntArrayMap<>();

    @Override
    public void registerFuel(Holder<? extends ItemLike> item, int fuelValue) {
        Objects.requireNonNull(item, "item is null");
        this.registerListenerIfNecessary();
        this.items.put(item, fuelValue);
    }

    @Override
    public void registerFuel(TagKey<Item> tagKey, int fuelValue) {
        Objects.requireNonNull(tagKey, "tag key is null");
        this.registerListenerIfNecessary();
        this.itemTags.put(tagKey, fuelValue);
    }

    protected abstract void registerListenerIfNecessary();
}
