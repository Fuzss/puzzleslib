package fuzs.puzzleslib.mixin.client.accessor;

import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.MultiPlayerGameMode;
import net.minecraft.client.multiplayer.prediction.PredictiveAction;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Invoker;

@Mixin(MultiPlayerGameMode.class)
public interface MultiPlayerGameModeFabricAccessor {

    @Invoker("startPrediction")
    void puzzleslib$callStartPrediction(ClientLevel clientLevel, PredictiveAction predictiveAction);
}
