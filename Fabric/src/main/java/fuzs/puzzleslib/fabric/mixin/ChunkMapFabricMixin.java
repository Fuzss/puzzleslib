package fuzs.puzzleslib.fabric.mixin;

import com.mojang.datafixers.DataFixer;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import net.minecraft.server.level.ChunkMap;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.level.ChunkPos;
import net.minecraft.world.level.chunk.LevelChunk;
import net.minecraft.world.level.chunk.storage.ChunkStorage;
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

    @Inject(method = "markChunkPendingToSend", at = @At("TAIL"))
    private static void markChunkPendingToSend(ServerPlayer player, LevelChunk chunk, CallbackInfo callback) {
        FabricLevelEvents.WATCH_CHUNK.invoker().onChunkWatch(player, chunk, player.serverLevel());
    }

    @Inject(method = "dropChunk", at = @At("HEAD"))
    private static void dropChunk(ServerPlayer player, ChunkPos chunkPos, CallbackInfo callback) {
        FabricLevelEvents.UNWATCH_CHUNK.invoker().onChunkUnwatch(player, chunkPos, player.serverLevel());
    }
}
