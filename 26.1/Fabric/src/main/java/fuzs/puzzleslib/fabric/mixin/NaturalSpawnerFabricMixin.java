package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.impl.event.PotentialSpawnsList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.Weighted;
import net.minecraft.util.random.WeightedList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.apache.commons.lang3.mutable.MutableObject;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerFabricMixin {

    @ModifyReturnValue(method = "mobsAt", at = @At("RETURN"))
    private static WeightedList<MobSpawnSettings.SpawnerData> mobsAt(WeightedList<MobSpawnSettings.SpawnerData> weightedList, ServerLevel serverLevel, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, @Nullable Holder<Biome> biome) {
        MutableObject<List<Weighted<MobSpawnSettings.SpawnerData>>> holder = new MutableObject<>();
        List<Weighted<MobSpawnSettings.SpawnerData>> mobs = new PotentialSpawnsList<>(() -> {
            return holder.get() != null ? holder.get() : weightedList.unwrap();
        }, (Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
            List<Weighted<MobSpawnSettings.SpawnerData>> spawnerDataList = holder.get();
            if (spawnerDataList == null) {
                holder.setValue(spawnerDataList = new ArrayList<>(weightedList.unwrap()));
            }

            return spawnerDataList.add(spawnerData);
        }, (Weighted<MobSpawnSettings.SpawnerData> spawnerData) -> {
            List<Weighted<MobSpawnSettings.SpawnerData>> spawnerDataList = holder.get();
            if (spawnerDataList == null) {
                holder.setValue(spawnerDataList = new ArrayList<>(weightedList.unwrap()));
            }

            return spawnerDataList.remove(spawnerData);
        });
        FabricLevelEvents.GATHER_POTENTIAL_SPAWNS.invoker()
                .onGatherPotentialSpawns(serverLevel, structureManager, chunkGenerator, mobCategory, blockPos, mobs);
        return holder.get() != null ? WeightedList.of(holder.get()) : weightedList;
    }
}
