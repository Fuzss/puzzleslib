package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.impl.core.context.GameplayContentContextFabricImpl;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.AxeItem;
import net.minecraft.world.item.DiggerItem;
import net.minecraft.world.item.Tier;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Optional;

@Mixin(AxeItem.class)
abstract class AxeItemFabricMixin extends DiggerItem {

    public AxeItemFabricMixin(Tier tier, TagKey<Block> blocks, Properties properties) {
        super(tier, blocks, properties);
    }

    @Inject(method = "getStripped", at = @At("HEAD"), cancellable = true)
    private void getStripped(BlockState state, CallbackInfoReturnable<Optional<BlockState>> callback) {
        GameplayContentContextFabricImpl.getStripped(state).map(Optional::of).ifPresent(callback::setReturnValue);
    }
}
