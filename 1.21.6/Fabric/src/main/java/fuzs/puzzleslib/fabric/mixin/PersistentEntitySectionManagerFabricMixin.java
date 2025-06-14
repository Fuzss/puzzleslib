package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricEntityEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
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
            if (FabricEntityEvents.ENTITY_LOAD.invoker()
                    .onEntityLoad(entity, (ServerLevel) entity.level(), !loadedFromDisk)
                    .isInterrupt()) {
                if (entity instanceof Player) {
                    // we do not support players as it isn't as straight-forward to implement for the server player on Fabric
                    throw new UnsupportedOperationException("Cannot prevent player from loading in!");
                } else {
                    callback.setReturnValue(false);
                }
            }
        }
    }
}
