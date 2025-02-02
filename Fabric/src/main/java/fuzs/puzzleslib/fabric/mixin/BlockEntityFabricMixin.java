package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;

@Mixin(BlockEntity.class)
abstract class BlockEntityFabricMixin {

    @ModifyReturnValue(method = "isValidBlockState", at = @At("TAIL"), require = 0)
    public boolean isValidBlockState(boolean isValidBlockState, BlockState blockState) {
        // allow 1.21.1 mods to work without any code changes
        return isValidBlockState || this.getType().isValid(blockState);
    }

    @Shadow
    public abstract BlockEntityType<?> getType();
}
