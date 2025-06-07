package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import net.minecraft.core.Holder;
import net.minecraft.core.HolderLookup;
import net.minecraft.core.RegistryAccess;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.damagesource.DamageType;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.level.LevelReader;
import org.jetbrains.annotations.Nullable;

/**
 * A small helper class for creating new {@link DamageSource DamageSources}, as vanilla's
 * {@link net.minecraft.world.damagesource.DamageSources} class does not allow for creating custom damage types.
 */
public final class DamageHelper {

    private DamageHelper() {
        // NO-OP
    }

    /**
     * Looks up a damage type holder in the provided registry access.
     *
     * @param entity      the registry access for retrieving the registry
     * @param resourceKey the value key
     * @return the holder from the registry
     */
    public static Holder<DamageType> lookup(Entity entity, ResourceKey<DamageType> resourceKey) {
        return LookupHelper.lookup(entity, Registries.DAMAGE_TYPE, resourceKey);
    }

    /**
     * Looks up a damage type holder in the provided registry access.
     *
     * @param levelReader the registry access for retrieving the registry
     * @param resourceKey the value key
     * @return the holder from the registry
     */
    public static Holder<DamageType> lookup(LevelReader levelReader, ResourceKey<DamageType> resourceKey) {
        return LookupHelper.lookup(levelReader, Registries.DAMAGE_TYPE, resourceKey);
    }

    /**
     * Looks up a damage type holder in the provided registry access.
     *
     * @param registries  the registry access for retrieving the registry
     * @param resourceKey the value key
     * @return the holder from the registry
     */
    public static Holder<DamageType> lookup(HolderLookup.Provider registries, ResourceKey<DamageType> resourceKey) {
        return LookupHelper.lookup(registries, Registries.DAMAGE_TYPE, resourceKey);
    }

    /**
     * Creates a new {@link DamageSource}.
     *
     * @param level      the registry access
     * @param damageType the damage type key
     * @return the new {@link DamageSource}
     */
    public static DamageSource damageSource(LevelReader level, ResourceKey<DamageType> damageType) {
        return damageSource(level, damageType, null, null);
    }

    /**
     * Creates a new {@link DamageSource}.
     *
     * @param level        the registry access
     * @param damageType   the damage type key
     * @param directEntity the entity directly responsible for causing damage, like the mob attacking the player, or the
     *                     arrow hitting the target
     * @return the new {@link DamageSource}
     */
    public static DamageSource damageSource(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity) {
        return damageSource(level, damageType, directEntity, directEntity);
    }

    /**
     * Creates a new {@link DamageSource}.
     *
     * @param level         the registry access
     * @param damageType    the damage type key
     * @param directEntity  the entity directly responsible for causing damage, like the mob attacking the player, or
     *                      the arrow hitting the target
     * @param causingEntity the entity that is responsible for causing damage to the target, like the player that shot
     *                      the arrow
     * @return the new {@link DamageSource}
     */
    public static DamageSource damageSource(LevelReader level, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return damageSource(level.registryAccess(), damageType, directEntity, causingEntity);
    }

    /**
     * Creates a new {@link DamageSource}.
     *
     * @param registries    the registry access
     * @param damageType    the damage type key
     * @param directEntity  the entity directly responsible for causing damage, like the mob attacking the player, or
     *                      the arrow hitting the target
     * @param causingEntity the entity that is responsible for causing damage to the target, like the player that shot
     *                      the arrow
     * @return the new {@link DamageSource}
     */
    public static DamageSource damageSource(RegistryAccess registries, ResourceKey<DamageType> damageType, @Nullable Entity directEntity, @Nullable Entity causingEntity) {
        return new DamageSource(lookup(registries, damageType), directEntity, causingEntity);
    }
}
