package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.fabric.api.event.v1.FabricPlayerEvents;
import net.minecraft.core.BlockPos;
import net.minecraft.world.item.BoneMealItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(BoneMealItem.class)
abstract class BoneMealItemFabricMixin extends Item {

    public BoneMealItemFabricMixin(Properties properties) {
        super(properties);
    }

    @Inject(method = "growCrop", at = @At("HEAD"), cancellable = true)
    private static void growCrop(ItemStack itemStack, Level level, BlockPos blockPos, CallbackInfoReturnable<Boolean> callbackInfo) {
        EventResult result = FabricPlayerEvents.USE_BONEMEAL.invoker()
                .onUseBoneMeal(level, blockPos, level.getBlockState(blockPos), itemStack);
        if (result.isInterrupt()) {
            callbackInfo.setReturnValue(result.getAsBoolean());
        }
    }
}
