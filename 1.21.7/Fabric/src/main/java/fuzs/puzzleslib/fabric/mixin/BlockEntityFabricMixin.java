package fuzs.puzzleslib.fabric.mixin;

import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BlockEntity.class)
abstract class BlockEntityFabricMixin {

    @Inject(method = "isValidBlockState", at = @At("HEAD"), cancellable = true)
    public void isValidBlockState(BlockState blockState, CallbackInfoReturnable<Boolean> callback) {
        // allow 1.21.1 mods to work without any code changes
        if (this.getType().isValid(blockState)) {
            callback.setReturnValue(true);
        }
    }

    @Shadow
    public abstract BlockEntityType<?> getType();
}
