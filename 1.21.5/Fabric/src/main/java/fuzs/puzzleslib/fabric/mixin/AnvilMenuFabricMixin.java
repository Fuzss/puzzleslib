package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.core.component.DataComponents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
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

    @Inject(
            method = "onTake", at = @At(
            value = "HEAD"
    )
    )
    protected void onTake$0(Player player, ItemStack itemStack, CallbackInfo callback, @Share("breakChance") LocalRef<DefaultedFloat> breakChanceRef) {
        breakChanceRef.set(DefaultedFloat.fromValue(0.12F));
        FabricPlayerEvents.ANVIL_USE.invoker().onAnvilUse(player, this.inputSlots.getItem(0),
                this.inputSlots.getItem(1), itemStack, breakChanceRef.get()
        );
    }

    @Inject(
            method = "onTake", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/ContainerLevelAccess;execute(Ljava/util/function/BiConsumer;)V"
    ), cancellable = true
    )
    protected void onTake$1(Player player, ItemStack itemStack, CallbackInfo callback, @Share("breakChance") LocalRef<DefaultedFloat> breakChanceRef) {
        // it is quite likely for other mods to change the break chance, so do not use @ModifyConstant
        // also saves us from having to inject into a lambda
        if (breakChanceRef.get().getAsOptionalFloat().isPresent()) {
            this.access.execute((Level level, BlockPos blockPos) -> {
                BlockState blockstate = level.getBlockState(blockPos);
                if (!player.getAbilities().instabuild && blockstate.is(BlockTags.ANVIL) &&
                        player.getRandom().nextFloat() < breakChanceRef.get().getAsFloat()) {
                    BlockState damagedBlockState = AnvilBlock.damage(blockstate);
                    if (damagedBlockState == null) {
                        level.removeBlock(blockPos, false);
                        level.levelEvent(LevelEvent.SOUND_ANVIL_BROKEN, blockPos, 0);
                    } else {
                        level.setBlock(blockPos, damagedBlockState, 2);
                        level.levelEvent(LevelEvent.SOUND_ANVIL_USED, blockPos, 0);
                    }
                } else {
                    level.levelEvent(LevelEvent.SOUND_ANVIL_USED, blockPos, 0);
                }
            });
            callback.cancel();
        }
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    public void createResult(CallbackInfo callback) {
        ItemStack leftInput = this.inputSlots.getItem(0);
        if (leftInput.isEmpty()) return;
        ItemStack rightInput = this.inputSlots.getItem(1);
        MutableValue<ItemStack> output = MutableValue.fromValue(ItemStack.EMPTY);
        MutableInt enchantmentCost = MutableInt.fromValue(leftInput.getOrDefault(DataComponents.REPAIR_COST, 0) +
                rightInput.getOrDefault(DataComponents.REPAIR_COST, 0));
        MutableInt materialCost = MutableInt.fromValue(0);
        EventResult result = FabricPlayerEvents.ANVIL_UPDATE.invoker().onAnvilUpdate(leftInput, rightInput, output,
                this.itemName, enchantmentCost, materialCost, this.player
        );
        if (result.isPass()) return;
        callback.cancel();
        if (!result.getAsBoolean()) return;
        this.resultSlots.setItem(0, output.get());
        this.cost.set(enchantmentCost.getAsInt());
        this.repairItemCountCost = materialCost.getAsInt();
    }
}
