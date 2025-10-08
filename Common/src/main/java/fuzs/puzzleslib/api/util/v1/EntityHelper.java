package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

/**
 * A helper class containing entity related methods.
 */
public final class EntityHelper {

    private EntityHelper() {
        // NO-OP
    }

    /**
     * Returns if an entity can equip some form of item in a certain slot.
     *
     * @param itemStack     the item stack to be equipped
     * @param equipmentSlot the slot the stack is trying to be equipped to
     * @param livingEntity  the entity trying to equip
     * @return is equipping the item stack allowed for the provided slot
     */
    public static boolean canEquip(ItemStack itemStack, EquipmentSlot equipmentSlot, LivingEntity livingEntity) {
        Objects.requireNonNull(itemStack, "item stack is null");
        Objects.requireNonNull(equipmentSlot, "equipment slot is null");
        Objects.requireNonNull(livingEntity, "living entity is null");
        return ProxyImpl.get().canEquip(itemStack, equipmentSlot, livingEntity);
    }

    /**
     * Retrieves a {@link EntitySpawnReason} from a {@link Mob} if it has been set during
     * {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)}.
     * <p>
     * Note that the spawn type is saved with the mob, so it persists across chunk and level reloads.
     *
     * @param entity the entity
     * @return the spawn type or null if none has been set or the entity is no {@link Mob}
     */
    public static @Nullable EntitySpawnReason getMobSpawnReason(Entity entity) {
        Objects.requireNonNull(entity, "entity is null");
        return entity instanceof Mob mob ? ProxyImpl.get().getMobSpawnReason(mob) : null;
    }

    /**
     * Called instead of directly checking {@link net.minecraft.world.level.GameRules#RULE_MOBGRIEFING}, allows for a
     * dedicated NeoForge event to run.
     *
     * @param serverLevel the level mob griefing is happening in
     * @param entity      the entity responsible for triggering the game rule check
     * @return is mob griefing allowed to happen
     */
    public static boolean isMobGriefingAllowed(ServerLevel serverLevel, @Nullable Entity entity) {
        Objects.requireNonNull(serverLevel, "server level is null");
        return ProxyImpl.get().isMobGriefingAllowed(serverLevel, entity);
    }

    /**
     * Get the parent mob from a possible mob part entity, like
     * {@link net.minecraft.world.entity.boss.EnderDragonPart}.
     * <p>
     * NeoForge allows extending this, so we need this abstraction.
     *
     * @param entity the mob, possibly a mob part
     * @return the parent mob for the part, otherwise the original entity
     */
    public static Entity getPartEntityParent(Entity entity) {
        Objects.requireNonNull(entity, "entity is null");
        return ProxyImpl.get().getPartEntityParent(entity);
    }

    /**
     * Checks if the provided player is a fake player.
     *
     * @param serverPlayer the server player
     * @return is the provided player a fake player
     */
    public static boolean isFakePlayer(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return ProxyImpl.get().isFakePlayer(serverPlayer);
    }

    /**
     * Will {@link net.minecraft.world.entity.monster.piglin.Piglin Piglins} give something back in exchange when given
     * this item.
     *
     * @param itemStack the item stack
     * @return is the item valid for bartering
     */
    public static boolean isPiglinCurrency(ItemStack itemStack) {
        return ProxyImpl.get().isPiglinCurrency(itemStack);
    }
}
