package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.impl.event.CapturedDropsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

@Mixin(Entity.class)
abstract class EntityFabricMixin implements CapturedDropsEntity {
    @Shadow
    @Nullable
    private Entity vehicle;
    @Shadow
    private Level level;
    @Unique
    @Nullable
    private Collection<ItemEntity> puzzleslib$capturedDrops;

    @Override
    public Collection<ItemEntity> puzzleslib$acceptCapturedDrops(Collection<ItemEntity> capturedDrops) {
        Collection<ItemEntity> oldCapturedDrops = this.puzzleslib$capturedDrops;
        this.puzzleslib$capturedDrops = capturedDrops;
        return oldCapturedDrops;
    }

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"), locals = LocalCapture.CAPTURE_FAILHARD, cancellable = true)
    public void spawnAtLocation(ItemStack stack, float offsetY, CallbackInfoReturnable<ItemEntity> callback, ItemEntity itemEntity) {
        Collection<ItemEntity> capturedDrops = this.puzzleslib$capturedDrops;
        if (capturedDrops != null) {
            capturedDrops.add(itemEntity);
            callback.setReturnValue(itemEntity);
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z", shift = At.Shift.BEFORE), cancellable = true)
    public void startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> callback) {
        // runs a little later than Forge when it is actually guaranteed for the rider to start riding
        EventResult result = FabricEntityEvents.ENTITY_START_RIDING.invoker().onStartRiding(this.level, Entity.class.cast(this), vehicle);
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"), cancellable = true)
    public void removeVehicle(CallbackInfo callback) {
        if (this.vehicle != null) {
            EventResult result = FabricEntityEvents.ENTITY_STOP_RIDING.invoker().onStopRiding(this.level, Entity.class.cast(this), this.vehicle);
            if (result.isInterrupt()) callback.cancel();
        }
    }
}
