package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.impl.event.CapturedDropsEntity;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.item.ItemEntity;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;
import org.spongepowered.asm.mixin.injection.callback.LocalCapture;

import java.util.Collection;

@Mixin(Entity.class)
abstract class EntityFabricMixin implements CapturedDropsEntity {
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
}
