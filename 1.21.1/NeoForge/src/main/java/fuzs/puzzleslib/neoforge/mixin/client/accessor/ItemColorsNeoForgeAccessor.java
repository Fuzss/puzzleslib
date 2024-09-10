package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemColors.class)
public interface ItemColorsNeoForgeAccessor {

    @Accessor("itemColors")
    Map<Item, ItemColor> puzzleslib$getItemColors();
}
