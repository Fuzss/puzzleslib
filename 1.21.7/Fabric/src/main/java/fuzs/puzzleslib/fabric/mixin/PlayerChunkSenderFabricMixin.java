package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.network.PlayerChunkSender;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.world.level.chunk.LevelChunk;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(PlayerChunkSender.class)
abstract class PlayerChunkSenderFabricMixin {

    @Inject(method = "sendChunk", at = @At("TAIL"))
    private static void sendChunk(ServerGamePacketListenerImpl packetListener, ServerLevel level, LevelChunk chunk, CallbackInfo callback) {
        FabricLevelEvents.WATCH_CHUNK.invoker().onChunkWatch(packetListener.getPlayer(), chunk, level);
    }
}
