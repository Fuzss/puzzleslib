package fuzs.puzzleslib.fabric.mixin;

import com.google.common.collect.Lists;
import com.llamalad7.mixinextras.injector.ModifyReturnValue;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
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

import java.util.Collections;
import java.util.List;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerFabricMixin {

    @ModifyReturnValue(method = "mobsAt", at = @At("TAIL"))
    private static WeightedRandomList<MobSpawnSettings.SpawnerData> mobsAt(WeightedRandomList<MobSpawnSettings.SpawnerData> weightedList, ServerLevel level, StructureManager structureManager, ChunkGenerator chunkGenerator, MobCategory mobCategory, BlockPos blockPos, @Nullable Holder<Biome> biome) {
        MutableBoolean mutableBoolean = MutableBoolean.fromValue(false);
        // implementation similar to Forge where only a view of the full list is provided
        List<MobSpawnSettings.SpawnerData> list = Lists.newArrayList(weightedList.unwrap());
        List<MobSpawnSettings.SpawnerData> mobs = new PotentialSpawnsList<>(Collections.unmodifiableList(list), spawnerData -> {
            mutableBoolean.accept(true);
            return list.add(spawnerData);
        }, spawnerData -> {
            mutableBoolean.accept(true);
            return list.remove(spawnerData);
        });
        FabricLevelEvents.GATHER_POTENTIAL_SPAWNS.invoker().onGatherPotentialSpawns(level, structureManager, chunkGenerator, mobCategory, blockPos, mobs);
        return mutableBoolean.getAsBoolean() ? WeightedRandomList.create(list) : weightedList;
    }
}
