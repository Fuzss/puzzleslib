package fuzs.puzzleslib.api.util.v1;

import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import net.minecraft.resources.ResourceKey;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.entity.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.storage.loot.LootParams;
import net.minecraft.world.level.storage.loot.LootTable;
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets;
import net.minecraft.world.level.storage.loot.parameters.LootContextParams;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Function;

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
     * Retrieves a {@link MobSpawnType} from a {@link Mob} if it has been set during
     * {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData)}.
     * <p>
     * Note that the spawn type is saved with the mob, so it persists across chunk and level reloads.
     *
     * @param entity the entity
     * @return the spawn type or null if none has been set or the entity is no {@link Mob}
     */
    @Deprecated
    public static @Nullable MobSpawnType getMobSpawnReason(Entity entity) {
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

    /**
     * Drops items generated from a gift loot table for an entity.
     * <p>
     * Copied from {@code LivingEntity::dropFromGiftLootTable} in Minecraft 26.1.
     *
     * @param entity       the entity providing the loot
     * @param level        the level
     * @param key          the loot table
     * @param dropConsumer the consumer for handling generated item stacks
     * @return whether any items were generated
     */
    public static boolean dropFromGiftLootTable(Entity entity, ServerLevel level, ResourceKey<LootTable> key, BiConsumer<ServerLevel, ItemStack> dropConsumer) {
        return dropFromLootTable(level,
                key,
                (LootParams.Builder params) -> params.withParameter(LootContextParams.ORIGIN, entity.position())
                        .withParameter(LootContextParams.THIS_ENTITY, entity)
                        .create(LootContextParamSets.GIFT),
                dropConsumer);
    }

    /**
     * Drops items generated from a shearing loot table for an entity.
     * <p>
     * Copied from {@code LivingEntity::dropFromShearingLootTable} in Minecraft 26.1.
     *
     * @param entity       the entity providing the loot
     * @param level        the level
     * @param key          the loot table
     * @param dropConsumer the consumer for handling generated item stacks
     * @return whether any items were generated
     */
    public static boolean dropFromShearingLootTable(Entity entity, ServerLevel level, ResourceKey<LootTable> key, BiConsumer<ServerLevel, ItemStack> dropConsumer) {
        return dropFromLootTable(level,
                key,
                (LootParams.Builder params) -> params.withParameter(LootContextParams.ORIGIN, entity.position())
                        .withParameter(LootContextParams.THIS_ENTITY, entity)
                        .create(LootContextParamSets.SHEARING),
                dropConsumer);
    }

    /**
     * Drops items generated from a loot table for an entity.
     * <p>
     * Copied from {@code LivingEntity::dropFromLootTable} in Minecraft 26.1.
     *
     * @param level         the level
     * @param key           the loot table
     * @param paramsBuilder the function for building the loot parameters
     * @param dropConsumer  the consumer for handling generated item stacks
     * @return whether any items were generated
     */
    public static boolean dropFromLootTable(ServerLevel level, ResourceKey<LootTable> key, Function<LootParams.Builder, LootParams> paramsBuilder, BiConsumer<ServerLevel, ItemStack> dropConsumer) {
        LootTable lootTable = level.getServer().reloadableRegistries().getLootTable(key);
        LootParams params = paramsBuilder.apply(new LootParams.Builder(level));
        List<ItemStack> drops = lootTable.getRandomItems(params);
        if (!drops.isEmpty()) {
            drops.forEach((ItemStack stack) -> dropConsumer.accept(level, stack));
            return true;
        } else {
            return false;
        }
    }
}
