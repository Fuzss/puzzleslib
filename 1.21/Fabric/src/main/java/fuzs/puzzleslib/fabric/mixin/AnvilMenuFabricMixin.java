package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import fuzs.puzzleslib.api.event.v1.data.MutableValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.tags.BlockTags;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.AnvilBlock;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(AnvilMenu.class)
abstract class AnvilMenuFabricMixin extends ItemCombinerMenu {
    @Shadow
    private int repairItemCountCost;
    @Shadow
    private String itemName;
    @Shadow
    @Final
    private DataSlot cost;

    public AnvilMenuFabricMixin(@Nullable MenuType<?> menuType, int i, Inventory inventory, ContainerLevelAccess containerLevelAccess) {
        super(menuType, i, inventory, containerLevelAccess);
    }

    @Inject(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/Container;setItem(ILnet/minecraft/world/item/ItemStack;)V"
            ),
            slice = @Slice(to = @At(value = "INVOKE", target = "Lnet/minecraft/world/item/ItemStack;getCount()I"))
    )
    protected void onTake$0(Player player, ItemStack itemStack, CallbackInfo callback, @Share("breakChance") LocalRef<MutableFloat> breakChanceRef) {
        if (!player.level().isClientSide) {
            breakChanceRef.set(MutableFloat.fromValue(0.12F));
            FabricPlayerEvents.ANVIL_USE.invoker()
                    .onAnvilUse(player,
                            this.inputSlots.getItem(0),
                            this.inputSlots.getItem(1),
                            itemStack,
                            breakChanceRef.get()
                    );
        }
    }

    @Inject(
            method = "onTake",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/DataSlot;set(I)V",
                    shift = At.Shift.AFTER
            ),
            cancellable = true
    )
    protected void onTake$1(Player player, ItemStack itemStack, CallbackInfo callback, @Share("breakChance") LocalRef<MutableFloat> breakChanceRef) {
        // just copy this part from vanilla, not in the mood to mixin into this lambda
        this.access.execute((level, blockPos) -> {
            BlockState blockState = level.getBlockState(blockPos);
            Objects.requireNonNull(breakChanceRef.get(), "break chance is null");
            float breakChance = breakChanceRef.get().getAsFloat();
            if (!player.getAbilities().instabuild && blockState.is(BlockTags.ANVIL) &&
                    player.getRandom().nextFloat() < breakChance) {
                BlockState blockState2 = AnvilBlock.damage(blockState);
                if (blockState2 == null) {
                    level.removeBlock(blockPos, false);
                    level.levelEvent(1029, blockPos, 0);
                } else {
                    level.setBlock(blockPos, blockState2, 2);
                    level.levelEvent(1030, blockPos, 0);
                }
            } else {
                level.levelEvent(1030, blockPos, 0);
            }

        });
        // always cancel vanilla, we handle everything
        callback.cancel();
    }

    @Inject(method = "createResult", at = @At("HEAD"), cancellable = true)
    public void createResult(CallbackInfo callback) {
        ItemStack leftInput = this.inputSlots.getItem(0);
        if (leftInput.isEmpty()) return;
        ItemStack rightInput = this.inputSlots.getItem(1);
        MutableValue<ItemStack> output = MutableValue.fromValue(ItemStack.EMPTY);
        MutableInt enchantmentCost = MutableInt.fromValue(
                leftInput.getBaseRepairCost() + rightInput.getBaseRepairCost());
        MutableInt materialCost = MutableInt.fromValue(0);
        EventResult result = FabricPlayerEvents.ANVIL_UPDATE.invoker()
                .onAnvilUpdate(leftInput,
                        rightInput,
                        output,
                        this.itemName,
                        enchantmentCost,
                        materialCost,
                        this.player
                );
        if (result.isPass()) return;
        callback.cancel();
        if (!result.getAsBoolean()) return;
        this.resultSlots.setItem(0, output.get());
        this.cost.set(enchantmentCost.getAsInt());
        this.repairItemCountCost = materialCost.getAsInt();
    }
}
