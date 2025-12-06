package fuzs.puzzleslib.fabric.impl.event;

import com.mojang.serialization.Codec;
import fuzs.puzzleslib.api.util.v1.CodecExtras;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.EntitySpawnReason;
import net.minecraft.world.entity.SpawnGroupData;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

/**
 * An extension to {@link net.minecraft.world.entity.Mob} that stores the {@link EntitySpawnReason} the mob was
 * initially spawned with when
 * {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason,
 * SpawnGroupData)} was called.
 * <p>
 * The implementation is just like NeoForge, but also allows for setting the spawn type in case e.g.
 * {@link net.minecraft.world.entity.Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason,
 * SpawnGroupData)} was overridden without calling {@code super}.
 */
public interface SpawnReasonMob {
    /**
     * Use {@link Enum#name()} for backward compatibility.
     */
    Codec<EntitySpawnReason> CODEC = CodecExtras.fromEnumWithMapping(EntitySpawnReason::values, Enum::name);

    @Nullable EntitySpawnReason puzzleslib$getSpawnReason();

    void puzzleslib$setSpawnReason(@Nullable EntitySpawnReason entitySpawnReason);
}
