package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableInt;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.ModifyVariable;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.Objects;

@Mixin(Block.class)
abstract class BlockFabricMixin extends BlockBehaviour {
    @Nullable
    private int[] puzzleslib$capturedExperience;
    
    public BlockFabricMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "playerDestroy", at = @At("HEAD"))
    public void playerDestroy$0(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack itemInHand, CallbackInfo callback) {
        this.puzzleslib$capturedExperience = new int[1];
    }

    @Inject(method = "playerDestroy", at = @At("TAIL"))
    public void playerDestroy$1(Level level, Player player, BlockPos pos, BlockState state, @Nullable BlockEntity blockEntity, ItemStack itemInHand, CallbackInfo callback) {
        int[] capturedExperience = this.puzzleslib$capturedExperience;
        this.puzzleslib$capturedExperience = null;
        Objects.requireNonNull(capturedExperience, "captured experience is null");
        MutableInt experienceToDrop = MutableInt.fromValue(capturedExperience[0]);
        FabricLevelEvents.DROP_BLOCK_EXPERIENCE.invoker().onDropExperience((ServerLevel) level, pos, state, player, itemInHand, experienceToDrop);
        if (experienceToDrop.getAsInt() > 0) this.popExperience((ServerLevel) level, pos, experienceToDrop.getAsInt());
    }

    @Shadow
    protected abstract void popExperience(ServerLevel level, BlockPos pos, int amount);

    @ModifyVariable(method = "tryDropExperience", at = @At("STORE"), ordinal = 0)
    protected int tryDropExperience(int i, ServerLevel level, BlockPos pos, ItemStack itemInHand) {
        int[] capturedExperience = this.puzzleslib$capturedExperience;
        if (capturedExperience != null) {
            capturedExperience[0] += i;
            // method will stop processing when zero is returned
            return 0;
        } else {
            return i;
        }
    }
}
