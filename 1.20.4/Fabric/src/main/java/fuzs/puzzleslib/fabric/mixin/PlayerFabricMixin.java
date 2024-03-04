package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

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

    @Inject(method = "hurt", at = @At("HEAD"), cancellable = true)
    public void hurt(DamageSource source, float amount, CallbackInfoReturnable<Boolean> callback) {
        EventResult result = FabricLivingEvents.LIVING_ATTACK.invoker().onLivingAttack(this, source, amount);
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @ModifyReturnValue(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("TAIL")
    )
    public ItemEntity drop(@Nullable ItemEntity itemEntity) {
        if (itemEntity != null &&
                FabricPlayerEvents.ITEM_TOSS.invoker().onItemToss(Player.class.cast(this), itemEntity).isInterrupt()) {
            return null;
        } else {
            return itemEntity;
        }
    }

    @ModifyReturnValue(method = "getDestroySpeed", at = @At("TAIL"), require = 0)
    public float getDestroySpeed(float destroySpeed, BlockState blockState) {
        // TODO remove require = 0 when ViaFabricPlus removes their @Overwrite
        DefaultedFloat defaultedFloat = DefaultedFloat.fromValue(destroySpeed);
        if (FabricPlayerEvents.BREAK_SPEED.invoker()
                .onBreakSpeed(Player.class.cast(this), blockState, defaultedFloat)
                .isInterrupt()) {
            defaultedFloat.accept(-1.0F);
        }

        return defaultedFloat.getAsOptionalFloat().orElse(destroySpeed);
    }

    @Inject(method = "actuallyHurt", at = @At("HEAD"), cancellable = true)
    protected void actuallyHurt(DamageSource damageSource, float damageAmount, CallbackInfo callback, @Share(
            "damageAmount"
    ) LocalRef<DefaultedFloat> damageAmountRef) {
        if (!this.isInvulnerableTo(damageSource)) {
            damageAmountRef.set(DefaultedFloat.fromValue(damageAmount));
            if (FabricLivingEvents.LIVING_HURT.invoker()
                    .onLivingHurt(LivingEntity.class.cast(this), damageSource, damageAmountRef.get())
                    .isInterrupt()) {
                callback.cancel();
            }
        }
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected float actuallyHurt(float damageAmount, DamageSource damageSource, @Share("damageAmount") LocalRef<DefaultedFloat> damageAmountRef) {
        if (!this.isInvulnerableTo(damageSource)) {
            Objects.requireNonNull(damageAmountRef.get(), "damage amount is null");
            damageAmount = damageAmountRef.get().getAsOptionalFloat().orElse(damageAmount);
        }
        return damageAmount;
    }
}
