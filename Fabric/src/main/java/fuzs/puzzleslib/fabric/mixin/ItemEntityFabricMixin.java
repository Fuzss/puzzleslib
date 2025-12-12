package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(ItemEntity.class)
abstract class ItemEntityFabricMixin extends Entity {
    @Shadow
    private int pickupDelay;
    @Shadow
    @Nullable
    private UUID target;

    public ItemEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void playerTouch$0(Player player, CallbackInfo callback, @Share("originalItemStack") LocalRef<ItemStack> originalItemStack) {
        if (!this.level().isClientSide()) {
            originalItemStack.set(ItemStack.EMPTY);
            if (this.pickupDelay > 0) {
                originalItemStack.set(this.getItem().copy());
                return;
            }
            ItemStack itemStack = this.getItem();
            Item item = itemStack.getItem();
            int count = itemStack.getCount();
            EventResult result = FabricPlayerEvents.ITEM_TOUCH.invoker()
                    .onItemTouch(player, ItemEntity.class.cast(this));
            if (!result.isInterrupt()) {
                originalItemStack.set(this.getItem().copy());
                return;
            } else {
                callback.cancel();
                if (!result.getAsBoolean()) {
                    return;
                }
            }
            if (this.pickupDelay == 0 && (this.target == null || this.target.equals(player.getUUID()))) {
                FabricPlayerEvents.ITEM_PICKUP.invoker()
                        .onItemPickup(player, ItemEntity.class.cast(this), itemStack.copy());
                player.take(this, count);
                if (itemStack.isEmpty()) {
                    this.discard();
                    itemStack.setCount(count);
                }

                player.awardStat(Stats.ITEM_PICKED_UP.get(item), count);
                player.onItemPickup(ItemEntity.class.cast(this));
            }
        }
    }

    @Inject(method = "playerTouch",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
    public void playerTouch$1(Player player, CallbackInfo callback, @Share("originalItemStack") LocalRef<ItemStack> originalItemStack) {
        ItemStack itemStack = originalItemStack.get();
        if (!itemStack.isEmpty()) {
            itemStack.setCount(itemStack.getCount() - this.getItem().getCount());
            FabricPlayerEvents.ITEM_PICKUP.invoker().onItemPickup(player, ItemEntity.class.cast(this), itemStack);
        }
    }

    @Shadow
    public abstract ItemStack getItem();
}
