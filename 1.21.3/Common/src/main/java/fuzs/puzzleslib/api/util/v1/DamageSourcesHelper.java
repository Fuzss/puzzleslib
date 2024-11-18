package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

/**
 * A small helper class for creating new {@link DamageSource} instances, as vanilla's
 * {@link net.minecraft.world.damagesource.DamageSources} does not allow for creating custom damage types.
 */
public final class DamageSourcesHelper {

    private DamageSourcesHelper() {
        // NO-OP
    }

    /**
     * Creates a new {@link DamageSource} instance.
     *
     * @param level      registry access for retrieving dynamic {@link DamageType} registry
     * @param damageType key for finding the {@link DamageType}
     * @return new {@link DamageSource} instance
     */
    public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType) {
        return source(level, damageType, null, null);
    }

    /**
     * Creates a new {@link DamageSource} instance.
     *
     * @param level        registry access for retrieving dynamic {@link DamageType} registry
     * @param damageType   key for finding the {@link DamageType}
     * @param directEntity the entity directly responsible for causing damage, like the mob attacking the player, or the
     *                     arrow hitting the target
     * @return new {@link DamageSource} instance
     */
    public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity) {
        return source(level, damageType, directEntity, directEntity);
    }

    /**
     * Creates a new {@link DamageSource} instance.
     *
     * @param level         registry access for retrieving dynamic {@link DamageType} registry
     * @param damageType    key for finding the {@link DamageType}
     * @param directEntity  the entity directly responsible for causing damage, like the mob attacking the player, or
     *                      the arrow hitting the target
     * @param causingEntity the entity that is responsible for <code>directEntity</code> to cause damage to the target,
     *                      like the player that shot the arrow
     * @return new {@link DamageSource} instance
     */
    public static DamageSource source(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return source(level.registryAccess(), damageType, directEntity, causingEntity);
    }

    /**
     * Creates a new {@link DamageSource} instance.
     *
     * @param registryAccess registry access for retrieving dynamic {@link DamageType} registry
     * @param damageType     key for finding the {@link DamageType}
     * @param directEntity   the entity directly responsible for causing damage, like the mob attacking the player, or
     *                       the arrow hitting the target
     * @param causingEntity  the entity that is responsible for <code>directEntity</code> to cause damage to the target,
     *                       like the player that shot the arrow
     * @return new {@link DamageSource} instance
     */
    public static DamageSource source(RegistryAccess registryAccess, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return new DamageSource(LookupHelper.lookup(registryAccess, Registries.DAMAGE_TYPE, damageType), directEntity,
                causingEntity
        );
    }
}
