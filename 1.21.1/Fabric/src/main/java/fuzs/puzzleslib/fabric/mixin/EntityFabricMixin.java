package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.v2.WrapWithCondition;
import com.llamalad7.mixinextras.sugar.Local;
import com.llamalad7.mixinextras.sugar.Share;
import com.llamalad7.mixinextras.sugar.ref.LocalBooleanRef;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.fabric.impl.event.CapturedDropsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityDimensions;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.Pose;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
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
    @Shadow
    private EntityDimensions dimensions;
    @Shadow
    private float eyeHeight;
    @Unique
    @Nullable
    private Collection<ItemEntity> puzzleslib$capturedDrops;

    @WrapWithCondition(method = "rideTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V"))
    public boolean rideTick(Entity entity, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        // avoid using @WrapOperation, so we are not blamed for any overhead from running the entity tick
        EventResult result = FabricEntityEvents.ENTITY_TICK_START.invoker().onStartEntityTick(entity);
        isEntityTickCancelled.set(result.isInterrupt());
        return result.isPass();
    }

    @Inject(method = "rideTick",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;tick()V", shift = At.Shift.AFTER))
    public void rideTick(CallbackInfo callback, @Share("isEntityTickCancelled") LocalBooleanRef isEntityTickCancelled) {
        if (!isEntityTickCancelled.get()) {
            FabricEntityEvents.ENTITY_TICK_END.invoker().onEndEntityTick(Entity.class.cast(this));
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

    @Inject(method = "spawnAtLocation(Lnet/minecraft/world/item/ItemStack;F)Lnet/minecraft/world/entity/item/ItemEntity;",
            at = @At(value = "INVOKE",
                    target = "Lnet/minecraft/world/level/Level;addFreshEntity(Lnet/minecraft/world/entity/Entity;)Z"),
            cancellable = true)
    public void spawnAtLocation(CallbackInfoReturnable<ItemEntity> callback, @Local ItemEntity itemEntity) {
        Collection<ItemEntity> capturedDrops = this.puzzleslib$getCapturedDrops();
        if (capturedDrops != null) {
            capturedDrops.add(itemEntity);
            callback.setReturnValue(itemEntity);
        }
    }

    @Inject(method = "startRiding(Lnet/minecraft/world/entity/Entity;Z)Z",
            at = @At(value = "INVOKE", target = "Lnet/minecraft/world/entity/Entity;isPassenger()Z"),
            cancellable = true)
    public void startRiding(Entity vehicle, boolean force, CallbackInfoReturnable<Boolean> callback) {
        // runs a little later than Forge when it is actually guaranteed for the rider to start riding
        EventResult result = FabricEntityEvents.ENTITY_START_RIDING.invoker()
                .onStartRiding(this.level, Entity.class.cast(this), vehicle);
        if (result.isInterrupt()) callback.setReturnValue(false);
    }

    @Inject(method = "removeVehicle", at = @At("HEAD"), cancellable = true)
    public void removeVehicle(CallbackInfo callback) {
        if (this.vehicle != null) {
            EventResult result = FabricEntityEvents.ENTITY_STOP_RIDING.invoker()
                    .onStopRiding(this.level, Entity.class.cast(this), this.vehicle);
            if (result.isInterrupt()) callback.cancel();
        }
    }

    @Inject(method = "<init>", at = @At("TAIL"))
    public void init(EntityType<?> entityType, Level level, CallbackInfo callback) {
        EventResultHolder<EntityDimensions> result = FabricEntityEvents.REFRESH_ENTITY_DIMENSIONS.invoker()
                .onRefreshEntityDimensions(Entity.class.cast(this), Pose.STANDING, entityType.getDimensions());
        result.ifInterrupt((EntityDimensions entityDimensions) -> {
            this.dimensions = entityDimensions;
            this.eyeHeight = entityDimensions.eyeHeight();
        });
    }

    @ModifyVariable(method = "refreshDimensions", at = @At("STORE"), ordinal = 1)
    public EntityDimensions refreshDimensions(EntityDimensions entityDimensions) {
        EventResultHolder<EntityDimensions> result = FabricEntityEvents.REFRESH_ENTITY_DIMENSIONS.invoker()
                .onRefreshEntityDimensions(Entity.class.cast(this), this.getPose(), entityDimensions);
        return result.getInterrupt().orElse(entityDimensions);
    }

    @Shadow
    public abstract Pose getPose();
}
