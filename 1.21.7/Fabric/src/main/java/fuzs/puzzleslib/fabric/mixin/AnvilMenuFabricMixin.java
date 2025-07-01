package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AnvilMenu.class)
abstract class AnvilMenuFabricMixin extends ItemCombinerMenu {
    @Shadow
    private int repairItemCountCost;
    @Shadow
    private String itemName;
    @Shadow
    @Final
    private DataSlot cost;

    public AnvilMenuFabricMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess, ItemCombinerMenuSlotDefinition itemCombinerMenuSlotDefinition) {
        super(menuType, i, inventory, containerLevelAccess, itemCombinerMenuSlotDefinition);
    }

    @Inject(method = "createResult", at = @At("RETURN"))
    public void createResult(CallbackInfo callback) {
        ItemStack primaryItemStack = this.inputSlots.getItem(0);
        ItemStack secondaryItemStack = this.inputSlots.getItem(1);
        MutableValue<ItemStack> outputItemStack = MutableValue.fromEvent((ItemStack itemStack) -> this.resultSlots.setItem(
                0,
                itemStack), () -> this.resultSlots.getItem(0));
        MutableInt enchantmentLevelCost = MutableInt.fromEvent(this.cost::set, this.cost::get);
        MutableInt repairMaterialCost = MutableInt.fromEvent((int i) -> this.repairItemCountCost = i,
                () -> this.repairItemCountCost);
        EventResult eventResult = FabricPlayerEvents.CREATE_ANVIL_RESULT.invoker()
                .onCreateAnvilResult(this.player,
                        primaryItemStack,
                        secondaryItemStack,
                        outputItemStack,
                        this.itemName,
                        enchantmentLevelCost,
                        repairMaterialCost);
        if (eventResult.isInterrupt()) {
            outputItemStack.accept(ItemStack.EMPTY);
            enchantmentLevelCost.accept(0);
            repairMaterialCost.accept(0);
        }
    }
}
