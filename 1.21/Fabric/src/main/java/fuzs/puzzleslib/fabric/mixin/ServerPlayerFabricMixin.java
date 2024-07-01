package fuzs.puzzleslib.fabric.mixin;

import com.mojang.authlib.GameProfile;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLivingEvents;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.Container;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.animal.horse.AbstractHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.OptionalInt;

@Mixin(ServerPlayer.class)
abstract class ServerPlayerFabricMixin extends Player {

    public ServerPlayerFabricMixin(Level level, BlockPos pos, float yRot, GameProfile gameProfile) {
        super(level, pos, yRot, gameProfile);
    }

    @Inject(
            method = "die", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/server/level/ServerPlayer;gameEvent(Lnet/minecraft/world/level/gameevent/GameEvent;)V",
            shift = At.Shift.AFTER
    ), cancellable = true
    )
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

    @Inject(
            method = "doCloseContainer", at = @At(
            value = "INVOKE",
            target = "Lnet/minecraft/world/inventory/InventoryMenu;transferState(Lnet/minecraft/world/inventory/AbstractContainerMenu;)V",
            shift = At.Shift.AFTER
    )
    )
    public void doCloseContainer(CallbackInfo callback) {
        FabricPlayerEvents.CONTAINER_CLOSE.invoker()
                .onContainerClose(ServerPlayer.class.cast(this), this.containerMenu);
    }
}
