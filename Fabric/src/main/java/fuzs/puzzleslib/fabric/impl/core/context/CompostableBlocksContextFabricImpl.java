package fuzs.puzzleslib.fabric.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.CompostableBlocksContext;
import net.fabricmc.fabric.api.registry.CompostingChanceRegistry;
import net.minecraft.core.Holder;
import net.minecraft.world.level.ItemLike;

import java.util.Objects;

@Deprecated
public final class CompostableBlocksContextFabricImpl implements CompostableBlocksContext {

    @Override
    public void registerCompostable(float compostingChance, Holder<? extends ItemLike>... items) {
        Preconditions.checkArgument(compostingChance >= 0.0F && compostingChance <= 1.0F,
                "Value " + compostingChance + " outside of range [" + 0.0F + ":" + 1.0F + "]");
        Objects.requireNonNull(items, "items is null");
        Preconditions.checkState(items.length > 0, "items is empty");
        for (Holder<? extends ItemLike> item : items) {
            Objects.requireNonNull(item, "item is null");
            CompostingChanceRegistry.INSTANCE.add(item.value(), compostingChance);
        }
    }
}
