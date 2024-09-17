package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.injector.wrapoperation.Operation;
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.fabric.impl.event.CapturedDropsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

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

    @WrapOperation(method = "rideTick", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    public void rideTick(Entity entity, Operation<Void> operation) {
        if (FabricEntityEvents.ENTITY_TICK_START.invoker().onStartEntityTick(entity).isPass()) {
            operation.call(entity);
            FabricEntityEvents.ENTITY_TICK_END.invoker().onEndEntityTick(entity);
        }
    }

    @Override
    public Collection<ItemEntity> puzzleslib$acceptCapturedDrops(Collection<ItemEntity> capturedDrops) {
        Collection<ItemEntity> oldCapturedDrops = this.puzzleslib$capturedDrops;
        this.puzzleslib$capturedDrops = capturedDrops;
        return oldCapturedDrops;
    }

    @Override
    public @Nullable Collection<ItemEntity> puzzleslib$getCapturedDrops() {
        return this.puzzleslib$capturedDrops;
    }

    @WrapWithCondition(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;", at = @At(value = "INVOKE", target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"))
    public boolean spawnAtLocation(Level level, Entity entity) {
        Collection<ItemEntity> capturedDrops = this.puzzleslib$getCapturedDrops();
        if (capturedDrops != null) {
            capturedDrops.add((ItemEntity) entity);
            return false;
        } else {
            return true;
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
