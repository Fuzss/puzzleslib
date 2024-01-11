package fuzs.puzzleslib.fabric.mixin.client.accessor;

import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen;
import net.minecraft.world.inventory.Slot;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(AbstractContainerScreen.class)
public interface AbstractContainerScreenFabricAccessor {

    @Accessor("imageWidth")
    int puzzleslib$getXSize();

    @Accessor("imageHeight")
    int puzzleslib$getYSize();

    @Accessor("leftPos")
    int puzzleslib$getGuiLeft();

    @Accessor("topPos")
    int puzzleslib$getGuiTop();

    @Accessor("hoveredSlot")
    Slot puzzleslib$getSlotUnderMouse();
}
