package fuzs.puzzleslib.fabric.mixin;

import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.impl.event.PotentialSpawnsList;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandomList;
import net.minecraft.world.entity.MobCategory;
import net.minecraft.world.level.NaturalSpawner;
import net.minecraft.world.level.StructureManager;
import net.minecraft.world.level.biome.Biome;
import net.minecraft.world.level.biome.MobSpawnSettings;
import net.minecraft.world.level.chunk.ChunkGenerator;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;

import java.util.ArrayList;
import java.util.List;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerFabricMixin {

    @ModifyReturnValue(method = "mobsAt", at = @At("TAIL"))
    private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(WeightedRandomList<MobSpawnSettings.SpawnerData> weightedList, ServerLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, @Nullable Holder<Biome> biome) {
        Object[] holder = new Object[1];
        List<MobSpawnSettings.SpawnerData> mobs = new PotentialSpawnsList<>(() -> {
            return holder[0] != null ? (List<MobSpawnSettings.SpawnerData>) holder[0] : weightedList.unwrap();
        }, spawnerData -> {
            List<MobSpawnSettings.SpawnerData> spawnerDataList = (List<MobSpawnSettings.SpawnerData>) holder[0];
            if (spawnerDataList == null) {
                holder[0] = spawnerDataList = new ArrayList<>(weightedList.unwrap());
            }
            return spawnerDataList.add(spawnerData);
        }, spawnerData -> {
            List<MobSpawnSettings.SpawnerData> spawnerDataList = (List<MobSpawnSettings.SpawnerData>) holder[0];
            if (spawnerDataList == null) {
                holder[0] = spawnerDataList = new ArrayList<>(weightedList.unwrap());
            }
            return spawnerDataList.remove(spawnerData);
        });
        FabricLevelEvents.GATHER_POTENTIAL_SPAWNS.invoker().onGatherPotentialSpawns(level, structureManager, chunkGenerator, mobCategory, blockPos, mobs);
        return holder[0] != null ? WeightedRandomList.create((List<MobSpawnSettings.SpawnerData>) holder[0]) : weightedList;
    }
}
