package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.Slice;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Player.class)
abstract class PlayerFabricMixin extends LivingEntity {

    protected PlayerFabricMixin(EntityType<? extends LivingEntity> entityType, Level level) {
        super(entityType, level);
    }

    @Inject(method = "tick", at = @At("HEAD"))
    public void tick$0(CallbackInfo callback) {
        FabricPlayerEvents.PLAYER_TICK_START.invoker().onStartPlayerTick(Player.class.cast(this));
    }

    @Inject(method = "tick", at = @At("TAIL"))
    public void tick$1(CallbackInfo callback) {
        FabricPlayerEvents.PLAYER_TICK_END.invoker().onEndPlayerTick(Player.class.cast(this));
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At("TAIL"), cancellable = true)
    public void drop(ItemStack itemStack, boolean throwRandomly, boolean retainOwnership, CallbackInfoReturnable<ItemEntity> callback) {
        ItemEntity itemEntity = callback.getReturnValue();
        if (itemEntity != null && FabricPlayerEvents.ITEM_TOSS.invoker().onItemToss(itemEntity, Player.class.cast(this)).isInterrupt()) {
            callback.setReturnValue(null);
        }
    }

    @ModifyVariable(method = "getDestroySpeed", at = @At(value = "LOAD", ordinal = 1), ordinal = 0, slice = @Slice(from = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/player/Player;onGround()Z")))
    public float getDestroySpeed(float destroySpeed, BlockState state) {
        // don't use inject and capture callback return value, it is likely for another mod to inject here and only one will be able to override the return value
        DefaultedFloat breakSpeed = DefaultedFloat.fromValue(destroySpeed);
        if (FabricPlayerEvents.BREAK_SPEED.invoker().onBreakSpeed(Player.class.cast(this), state, breakSpeed).isInterrupt()) {
            breakSpeed.accept(-1.0F);
        }
        return breakSpeed.getAsOptionalFloat().orElse(destroySpeed);
    }
}
