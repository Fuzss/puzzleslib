package fuzs.puzzleslib.mixin.accessor;

import net.minecraft.world.Container;
import net.minecraft.world.inventory.GrindstoneMenu;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(GrindstoneMenu.class)
public interface GrindstoneMenuFabricAccessor {

    @Accessor("repairSlots")
    Container puzzleslib$getRepairSlots();
}
