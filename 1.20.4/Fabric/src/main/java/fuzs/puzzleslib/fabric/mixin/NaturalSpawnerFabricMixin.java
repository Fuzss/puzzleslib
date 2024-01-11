package fuzs.puzzleslib.fabric.mixin;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.Lists;
import fuzs.puzzleslib.fabric.api.event.v1.FabricLevelEvents;
import fuzs.puzzleslib.api.event.v1.data.MutableBoolean;
import fuzs.puzzleslib.fabric.mixin.accessor.WeightedRandomListFabricAccessor;
import fuzs.puzzleslib.impl.event.PotentialSpawnsList;
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

import java.util.Collections;
import java.util.List;

@Mixin(NaturalSpawner.class)
abstract class NaturalSpawnerFabricMixin {

    @Inject(method = "mobsAt", at = @At("TAIL"))
    private static void mobsAt(ServerLevel level, StructureManager structureManager, ChunkGenerator generator, MobCategory category, BlockPos pos, @Nullable Holder<Biome> biome, CallbackInfoReturnable<WeightedRandomList<MobSpawnSettings.SpawnerData>> callback) {
        MutableBoolean mutableBoolean = MutableBoolean.fromValue(false);
        WeightedRandomList<MobSpawnSettings.SpawnerData> weightedList = callback.getReturnValue();
        List<MobSpawnSettings.SpawnerData> list = Lists.newArrayList(weightedList.unwrap());
        List<MobSpawnSettings.SpawnerData> mobsAt = new PotentialSpawnsList<>(Collections.unmodifiableList(list), spawnerData -> {
            mutableBoolean.accept(true);
            return list.add(spawnerData);
        }, spawnerData -> {
            mutableBoolean.accept(true);
            return list.remove(spawnerData);
        });
        FabricLevelEvents.GATHER_POTENTIAL_SPAWNS.invoker().onGatherPotentialSpawns(level, structureManager, generator, category, pos, mobsAt);
        // try not to replace the return value weighted list, instead change it in hopes of better compatibility with other mixins
        if (mutableBoolean.getAsBoolean()) {
            ((WeightedRandomListFabricAccessor<MobSpawnSettings.SpawnerData>) weightedList).puzzleslib$setTotalWeight(WeightedRandom.getTotalWeight(list));
            ((WeightedRandomListFabricAccessor<MobSpawnSettings.SpawnerData>) weightedList).puzzleslib$setItems(ImmutableList.copyOf(list));
        }
    }
}
