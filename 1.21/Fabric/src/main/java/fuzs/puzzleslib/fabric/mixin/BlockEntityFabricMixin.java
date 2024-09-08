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

    @ModifyReturnValue(method = {"isValidBlockState", "method_61176"}, at = @At("TAIL"), remap = false, require = 0)
    public boolean isValidBlockState(boolean isValidBlockState, BlockState blockState) {
        // TODO preliminary 1.21 compat, adjust mixin values when properly updating
        return isValidBlockState || this.getType().isValid(blockState);
    }

    @Shadow
    public abstract BlockEntityType<?> getType();
}
