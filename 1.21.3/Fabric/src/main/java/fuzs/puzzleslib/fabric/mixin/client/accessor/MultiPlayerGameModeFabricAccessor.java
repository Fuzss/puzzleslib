package fuzs.puzzleslib.fabric.mixin.client.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import net.minecraft.core.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeFabricAccessor {

    @Accessor("isDestroying")
    boolean puzzleslib$getIsDestroying();

    @Accessor("isDestroying")
    void puzzleslib$setIsDestroying(boolean isDestroying);

    @Invoker("sameDestroyTarget")
    boolean puzzleslib$callSameDestroyTarget(BlockPos pos);

    @Invoker("startPrediction")
    void puzzleslib$callStartPrediction(ClientLevel clientLevel, PredictiveAction predictiveAction);
}
