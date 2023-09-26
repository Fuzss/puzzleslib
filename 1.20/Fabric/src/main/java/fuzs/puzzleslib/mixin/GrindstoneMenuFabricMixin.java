package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.impl.event.EventImplHelper;
import fuzs.puzzleslib.impl.event.GrindstoneXpHolder;
import net.minecraft.world.Container;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(GrindstoneMenu.class)
abstract class GrindstoneMenuFabricMixin extends AbstractContainerMenu implements GrindstoneXpHolder {
    @Shadow
    @Final
    private Container resultSlots;
    @Shadow
    @Final
    Container repairSlots;
    @Unique
    private int puzzleslib$xp = -1;

    protected GrindstoneMenuFabricMixin(@Nullable MenuType<?> menuType, int containerId) {
        super(menuType, containerId);
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    private void createResult(CallbackInfo callback) {
        ItemStack topInput = this.repairSlots.getItem(0);
        ItemStack bottomInput = this.repairSlots.getItem(1);
        MutableValue<ItemStack> output = MutableValue.fromValue(ItemStack.EMPTY);
        MutableInt experienceReward = MutableInt.fromValue(this.puzzleslib$xp);
        Player player = EventImplHelper.getPlayerFromContainerMenu(this).orElseThrow(NullPointerException::new);
        EventResult result = FabricPlayerEvents.GRINDSTONE_UPDATE.invoker().onGrindstoneUpdate(topInput, bottomInput, output, experienceReward, player);
        if (result.isInterrupt()) {
            if (result.getAsBoolean()) {
                this.resultSlots.setItem(0, output.get());
                this.puzzleslib$xp = experienceReward.getAsInt();
                this.broadcastChanges();
            } else {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
                this.puzzleslib$xp = -1;
            }
            callback.cancel();
        } else {
            this.puzzleslib$xp = -1;
        }
    }

    @Override
    public int puzzleslib$getXp() {
        return this.puzzleslib$xp;
    }
}
