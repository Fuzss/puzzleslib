package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.client.color.item.ItemColor;
import net.minecraft.client.color.item.ItemColors;
import net.minecraft.world.item.Item;
import net.minecraftforge.registries.IRegistryDelegate;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.Map;

@Mixin(ItemColors.class)
public interface ItemColorsForgeAccessor {

    @Accessor("itemColors")
    Map<IRegistryDelegate<Item>, ItemColor> puzzleslib$getItemColors();
}
