package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.stats.Stats;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
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
    @Unique
    private ItemStack puzzleslib$originalItem = ItemStack.EMPTY;

    public ItemEntityFabricMixin(EntityType<?> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "playerTouch", at = @At("HEAD"), cancellable = true)
    public void playerTouch$0(Player player, CallbackInfo callback) {
        if (!this.level().isClientSide) {
            if (this.pickupDelay > 0) {
                this.puzzleslib$originalItem = this.getItem().copy();
                return;
            }
            ItemStack itemStack = this.getItem();
            Item item = itemStack.getItem();
            int i = itemStack.getCount();
            EventResult result = FabricPlayerEvents.ITEM_TOUCH.invoker().onItemTouch(player, ItemEntity.class.cast(this));
            if (!result.isInterrupt()) {
                this.puzzleslib$originalItem = this.getItem().copy();
                return;
            } else {
                callback.cancel();
                if (!result.getAsBoolean()) {
                    return;
                }
            }
            if (this.pickupDelay == 0 && (this.target == null || this.target.equals(player.getUUID()))) {
                FabricPlayerEvents.ITEM_PICKUP.invoker().onItemPickup(player, ItemEntity.class.cast(this), itemStack.copy());
                player.take(this, i);
                if (itemStack.isEmpty()) {
                    this.discard();
                    itemStack.setCount(i);
                }

                player.awardStat(Stats.ITEM_PICKED_UP.get(item), i);
                player.onItemPickup(ItemEntity.class.cast(this));
            }
        }
    }

    @Inject(method = "playerTouch", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;take(Lnet/minecraft/world/entity/Entity;I)V"))
    public void playerTouch$1(Player player, CallbackInfo callback) {
        if (this.puzzleslib$originalItem.isEmpty()) return;
        ItemStack copy = this.puzzleslib$originalItem;
        copy.setCount(copy.getCount() - this.getItem().getCount());
        FabricPlayerEvents.ITEM_PICKUP.invoker().onItemPickup(player, ItemEntity.class.cast(this), copy);
        this.puzzleslib$originalItem = ItemStack.EMPTY;
    }

    @Shadow
    public abstract ItemStack getItem();
}
