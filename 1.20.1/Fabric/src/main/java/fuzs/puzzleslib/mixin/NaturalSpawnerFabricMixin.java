package fuzs.puzzleslib.mixin;

import com.google.common.collect.ImmutableList;
import fuzs.puzzleslib.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.impl.event.PotentialSpawnsList;
import fuzs.puzzleslib.mixin.accessor.WeightedRandomListFabricAccessor;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.util.random.WeightedRandom;
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
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.List;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerFabricMixin {

    @Inject(method = "mobsAt", at = @At("TAIL"))
    private static void mobsAt(ServerLevel level, StructureManager structureManager, ChunkGenerator generator, MobCategory category, BlockPos pos, @Nullable Holder<Biome> biome, CallbackInfoReturnable<WeightedRandomList<MobSpawnSettings.SpawnerData>> callback) {
        Object[] holder = new Object[1];
        WeightedRandomList<MobSpawnSettings.SpawnerData> weightedList = callback.getReturnValue();
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
        FabricLevelEvents.GATHER_POTENTIAL_SPAWNS.invoker().onGatherPotentialSpawns(level, structureManager, generator, category, pos, mobs);
        // try not to replace the return value weighted list, instead change it in hopes of better compatibility with other mixins
        // no longer necessary when using Mixin Extras
        if (holder[0] != null) {
            List<MobSpawnSettings.SpawnerData> spawnerDataList = (List<MobSpawnSettings.SpawnerData>) holder[0];
            ((WeightedRandomListFabricAccessor<MobSpawnSettings.SpawnerData>) weightedList).puzzleslib$setTotalWeight(WeightedRandom.getTotalWeight(spawnerDataList));
            ((WeightedRandomListFabricAccessor<MobSpawnSettings.SpawnerData>) weightedList).puzzleslib$setItems(ImmutableList.copyOf(spawnerDataList));
        }
    }
}
