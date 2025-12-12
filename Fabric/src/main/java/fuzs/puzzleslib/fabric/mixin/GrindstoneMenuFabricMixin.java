package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyExpressionValue;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.fabric.impl.event.GrindstoneExperienceHolder;
import fuzs.puzzleslib.impl.event.EventImplHelper;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;

import java.util.OptionalInt;

@Mixin(GrindstoneMenu.class)
abstract class GrindstoneMenuFabricMixin extends AbstractContainerMenu implements GrindstoneExperienceHolder {
    @Shadow
    @Final
    Container repairSlots;
    @Unique
    private OptionalInt puzzleslib$experiencePointReward = OptionalInt.empty();

    protected GrindstoneMenuFabricMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @ModifyExpressionValue(
            method = "createResult", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/GrindstoneMenu;computeResult(Lnet/minecraft/world/item/ItemStack;Lnet/minecraft/world/item/ItemStack;)Lnet/minecraft/world/item/ItemStack;"
    )
    )
    private ItemStack createResult(ItemStack itemStack) {
        Player player = EventImplHelper.getPlayerFromContainerMenu(this);
        if (player != null) {
            ItemStack primaryItemStack = this.repairSlots.getItem(0);
            ItemStack secondaryItemStack = this.repairSlots.getItem(1);
            MutableValue<ItemStack> outputItemStack = MutableValue.fromValue(itemStack);
            MutableInt experiencePointReward = MutableInt.fromEvent((int i) -> this.puzzleslib$experiencePointReward = OptionalInt.of(
                    i), () -> 0);
            EventResult eventResult = FabricPlayerEvents.CREATE_GRINDSTONE_RESULT.invoker()
                    .onCreateGrindstoneResult(player,
                            primaryItemStack,
                            secondaryItemStack,
                            outputItemStack,
                            experiencePointReward);
            if (eventResult.isInterrupt()) {
                this.puzzleslib$experiencePointReward = OptionalInt.empty();
                return ItemStack.EMPTY;
            } else {
                return outputItemStack.get();
            }
        } else {
            return itemStack;
        }
    }

    @Override
    public OptionalInt puzzleslib$getExperiencePointReward() {
        return this.puzzleslib$experiencePointReward;
    }
}
