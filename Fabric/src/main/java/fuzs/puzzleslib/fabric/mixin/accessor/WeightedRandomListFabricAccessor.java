package fuzs.puzzleslib.fabric.mixin.accessor;

import com.google.common.collect.ImmutableList;
import net.minecraft.util.random.WeightedEntry;
import net.minecraft.util.random.WeightedRandomList;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Mutable;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(WeightedRandomList.class)
public interface WeightedRandomListFabricAccessor<E extends WeightedEntry> {

    @Accessor("totalWeight")
    @Mutable
    void puzzleslib$setTotalWeight(int totalWeight);

    @Accessor("items")
    @Mutable
    void puzzleslib$setItems(ImmutableList<E> items);
}
