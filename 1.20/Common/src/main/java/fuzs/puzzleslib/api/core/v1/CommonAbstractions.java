package fuzs.puzzleslib.api.core.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * useful methods for gameplay related things that require mod loader specific abstractions
 */
public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    /**
     * Get the current game server which is captured to allow retrieval from anywhere.
     *
     * @return current game server, null when not in a world
     */
    MinecraftServer getGameServer();

    /**
     * opens a menu on both client and server
     *
     * @param player       player to open menu for
     * @param menuProvider menu factory
     */
    default void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        this.openMenu(player, menuProvider, (ServerPlayer serverPlayer, FriendlyByteBuf buf) -> {});
    }

    /**
     * opens a menu on both client and server while also providing additional data
     *
     * @param player                  player to open menu for
     * @param menuProvider            menu factory
     * @param screenOpeningDataWriter additional data added via {@link FriendlyByteBuf}
     */
    void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, FriendlyByteBuf> screenOpeningDataWriter);

    /**
     * Is <code>entityType</code> considered a boss mob like {@link EntityType#ENDER_DRAGON} and {@link EntityType#WITHER} in vanilla.
     * <p>A common property of such entities usually is {@link LivingEntity#canChangeDimensions()} returns <code>false</code>.
     *
     * @param type the entity type
     * @return is it a boss mob
     */
    boolean isBossMob(EntityType<?> type);

    /**
     * Returns the enchanting power provided by a block. An enchanting power of 15 is required for level 30 enchants,
     * each bookshelf block provides exactly one enchanting power.
     *
     * @param state the block state at the given position
     * @param level the level
     * @param pos   the block position in the level
     * @return enchanting power, usually zero for blocks other than bookshelves
     */
    float getEnchantPowerBonus(BlockState state, Level level, BlockPos pos);

    /**
     * Returns if an entity can equip some form of item in a certain slot.
     *
     * @param stack  the stack to be equipped
     * @param slot   the slot the stack is trying to be equipped to
     * @param entity the entity trying to equip
     * @return is equipping this <code>stack</code> to <code>slot</code> allowed for <code>entity</code>
     */
    boolean canEquip(ItemStack stack, EquipmentSlot slot, Entity entity);

    /**
     * Called before an entity drops loot for determining the level of {@link net.minecraft.world.item.enchantment.Enchantments#MOB_LOOTING} to apply when generating drops.
     *
     * @param entity       the entity that has been killed
     * @param killerEntity another entity responsible for killing <code>entity</code>
     * @param damageSource the damage source <code>entity</code> has been killed by
     * @return the level of looting to apply when generating drops.
     */
    int getMobLootingLevel(Entity entity, @Nullable Entity killerEntity, @Nullable DamageSource damageSource);

    /**
     * Called when a <code>mobGriefing</code> game rule check is required instead of vanilla's <code>level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)</code>,
     * allowing for a dedicated Forge event to run.
     *
     * @param level  the level mob griefing is happening in
     * @param entity the entity responsible for triggering the game rule check
     * @return is mob griefing allows to happen
     */
    boolean getMobGriefingRule(Level level, @Nullable Entity entity);

    /**
     * A trigger for running a Forge event for destroying an item.
     * <p>Ideally this should be migrated to the event api, to also allow for firing the event on Fabric. Until that happens this functions as a workaround.
     *
     * @param player          the player destroying the item
     * @param itemStack       the item stack before being destroyed
     * @param interactionHand the hand holding the destroyed stack
     */
    void onPlayerDestroyItem(Player player, ItemStack itemStack, @Nullable InteractionHand interactionHand);

    /**
     * Retrieves a {@link MobSpawnType} from a {@link Mob} if it has been set during {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, MobSpawnType, SpawnGroupData, CompoundTag)}.
     * <p>Note that the spawn type is saved with the mob, so it persists across chunk and level reloads.
     *
     * @param mob the mob
     * @return the spawn type or null if none has been set
     */
    @Nullable
    MobSpawnType getMobSpawnType(Mob mob);
}
