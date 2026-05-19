package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import fuzs.puzzleslib.fabric.impl.event.EntityLoadData;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.entity.EntityAccess;
import net.minecraft.world.level.entity.PersistentEntitySectionManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(PersistentEntitySectionManager.class)
abstract class PersistentEntitySectionManagerFabricMixin<T extends EntityAccess> {

    @Inject(method = "addEntity", at = @At("HEAD"), cancellable = true)
    private void addEntity(T entityAccess, boolean loadedFromDisk, CallbackInfoReturnable<Boolean> callback) {
        if (entityAccess instanceof Entity entity) {
            if (entity instanceof EntityLoadData loadData) {
                loadData.puzzleslib$setLoadedFromDisk(loadedFromDisk);
            }

            EventResult eventResult = FabricEntityEvents.ENTITY_LOAD.invoker()
                    .onEntityLoad(entity, (ServerLevel) entity.level());
            if (eventResult.isInterrupt()) {
                callback.setReturnValue(false);
            }
        }
    }
}
