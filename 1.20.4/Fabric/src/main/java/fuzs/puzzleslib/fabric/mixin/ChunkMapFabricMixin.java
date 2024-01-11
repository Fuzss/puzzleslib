package fuzs.puzzleslib.fabric.mixin;

import com.mojang.datafixers.DataFixer;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import net.minecraft.network.protocol.game.ClientboundLevelChunkWithLightPacket;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
import org.apache.commons.lang3.mutable.MutableObject;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.nio.file.Path;

@Mixin(ChunkMap.class)
abstract class ChunkMapFabricMixin extends ChunkStorage {
    @Shadow
    @Final
    ServerLevel level;

    public ChunkMapFabricMixin(Path regionFolder, DataFixer fixerUpper, boolean sync) {
        super(regionFolder, fixerUpper, sync);
    }

    @Inject(method = "updateChunkTracking", at = @At(value = "INVOKE", target = "Lnet/minecraft/server/level/ServerPlayer;untrackChunk(Lnet/minecraft/world/level/ChunkPos;)V", shift = At.Shift.AFTER))
    protected void updateChunkTracking(ServerPlayer player, ChunkPos chunkPos, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, boolean wasLoaded, boolean load, CallbackInfo callback) {
        FabricLevelEvents.UNWATCH_CHUNK.invoker().onChunkUnwatch(player, chunkPos, this.level);
    }

    @Inject(method = "playerLoadedChunk", at = @At("TAIL"))
    private void playerLoadedChunk(ServerPlayer player, MutableObject<ClientboundLevelChunkWithLightPacket> packetCache, LevelChunk chunk, CallbackInfo callback) {
        FabricLevelEvents.WATCH_CHUNK.invoker().onChunkWatch(player, chunk, this.level);
    }
}
