package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import com.llamalad7.mixinextras.sugar.Cancellable;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.DefaultedFloat;
import fuzs.puzzleslib.api.event.v1.data.DefaultedValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.fabric.impl.event.FabricEventImplHelper;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.ProjectileWeaponItem;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import org.apache.commons.lang3.mutable.MutableBoolean;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

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

    @ModifyReturnValue(
            method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At("TAIL")
    )
    public ItemEntity drop(@Nullable ItemEntity itemEntity) {
        if (itemEntity != null && FabricPlayerEvents.ITEM_TOSS.invoker()
                .onItemToss(Player.class.cast(this), itemEntity)
                .isInterrupt()) {
            return null;
        } else {
            return itemEntity;
        }
    }

    @ModifyReturnValue(method = "getDestroySpeed", at = @At("TAIL"))
    public float getDestroySpeed(float destroySpeed, BlockState blockState) {
        DefaultedFloat defaultedFloat = DefaultedFloat.fromValue(destroySpeed);
        if (FabricPlayerEvents.BREAK_SPEED.invoker()
                .onBreakSpeed(Player.class.cast(this), blockState, defaultedFloat)
                .isInterrupt()) {
            defaultedFloat.accept(-1.0F);
        }

        return defaultedFloat.getAsOptionalFloat().orElse(destroySpeed);
    }

    @Inject(
            method = "die", at = @At(
            "HEAD"
    ), cancellable = true
    )
    public void die(DamageSource damageSource, CallbackInfo callback) {
        // this will fire twice for players, since the die method calls super on LivingEntity, where this is hooked in again
        // Forge has it implemented like this, so let's leave it for now for parity
        // can't easily filter out the second call on Forge unfortunately
        EventResult result = FabricLivingEvents.LIVING_DEATH.invoker().onLivingDeath(this, damageSource);
        if (result.isInterrupt()) callback.cancel();
    }

    @ModifyVariable(method = "actuallyHurt", at = @At("HEAD"), ordinal = 0, argsOnly = true)
    protected float actuallyHurt(float damageAmount, ServerLevel serverLevel, DamageSource damageSource, @Cancellable CallbackInfo callback) {
        if (!this.isInvulnerableTo(serverLevel, damageSource)) {
            MutableBoolean cancelInjection = new MutableBoolean();
            damageAmount = FabricEventImplHelper.onLivingHurt(this, serverLevel, damageSource, damageAmount,
                    cancelInjection
            );
            if (cancelInjection.booleanValue()) callback.cancel();
        }

        return damageAmount;
    }

    @ModifyReturnValue(method = "getProjectile", at = @At("RETURN"))
    public ItemStack getProjectile(ItemStack projectileItemStack, ItemStack weaponItemStack) {
        if (weaponItemStack.getItem() instanceof ProjectileWeaponItem) {
            DefaultedValue<ItemStack> projectileItemStackValue = DefaultedValue.fromValue(projectileItemStack);
            FabricLivingEvents.PICK_PROJECTILE.invoker().onPickProjectile(this, weaponItemStack, projectileItemStackValue);
            return projectileItemStackValue.getAsOptional().orElse(projectileItemStack);
        } else {
            return projectileItemStack;
        }
    }
}
