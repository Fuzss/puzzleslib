package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.sugar.Local;
import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import fuzs.puzzleslib.fabric.impl.event.CapturedDropsEntity;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Collection;
import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerFabricMixin extends Player implements CapturedDropsEntity {

    public ServerPlayerFabricMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(method = "drop(Lnet/minecraft/world/item/ItemStack;ZZ)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            cancellable = true)
    public void drop(CallbackInfoReturnable<ItemEntity> callback, @Local ItemEntity itemEntity) {
        Collection<ItemEntity> capturedDrops = this.puzzleslib$getCapturedDrops();
        if (capturedDrops != null) {
            capturedDrops.add(itemEntity);
            callback.setReturnValue(itemEntity);
        }
    }

    @Inject(method = "die", at = @At("HEAD"), cancellable = true)
    public void die(DamageSource damageSource, CallbackInfo callback) {
        EventResult result = FabricLivingEvents.LIVING_DEATH.invoker().onLivingDeath(this, damageSource);
        if (result.isInterrupt()) callback.cancel();
    }

    @Inject(method = "openMenu", at = @At("TAIL"))
    public void openMenu(@Nullable MenuProvider menu, CallbackInfoReturnable<OptionalInt> callback) {
        FabricPlayerEvents.CONTAINER_OPEN.invoker().onContainerOpen(ServerPlayer.class.cast(this), this.containerMenu);
    }

    @Inject(method = "openHorseInventory", at = @At("TAIL"))
    public void openHorseInventory(AbstractHorse horse, Container inventory, CallbackInfo callback) {
        FabricPlayerEvents.CONTAINER_OPEN.invoker().onContainerOpen(ServerPlayer.class.cast(this), this.containerMenu);
    }

    @Inject(method = "doCloseContainer",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/inventory/InventoryMenu;transferState(Lnet/minecraft/world/inventory/AbstractContainerMenu;)V",
                    shift = At.Shift.AFTER))
    public void doCloseContainer(CallbackInfo callback) {
        FabricPlayerEvents.CONTAINER_CLOSE.invoker()
                .onContainerClose(ServerPlayer.class.cast(this), this.containerMenu);
    }
}
