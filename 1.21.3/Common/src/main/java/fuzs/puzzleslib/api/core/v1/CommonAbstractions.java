package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.api.init.v3.registry.LookupHelper;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Holder;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.world.DifficultyInstance;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.damagesource.DamageSource;
import net.minecraft.world.entity.*;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.flag.FeatureFlagSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.item.enchantment.Enchantments;
import net.minecraft.world.level.Explosion;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.ServerExplosion;
import net.minecraft.world.level.ServerLevelAccessor;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;

/**
 * Useful methods for gameplay related things that require mod loader specific abstractions.
 */
public interface CommonAbstractions {
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    /**
     * Get the current game server which is captured to allow retrieval from anywhere.
     *
     * @return current game server, null when not in a world
     */
    MinecraftServer getMinecraftServer();

    /**
     * Checks if the connected client declared the ability to receive a specific type of packet.
     *
     * @param serverPlayer the server player
     * @param type         the packet type
     * @return if the connected client has declared the ability to receive a specific type of packet
     */
    boolean hasChannel(ServerPlayer serverPlayer, CustomPacketPayload.Type<?> type);

    /**
     * Opens a menu on both client and server while also providing additional data.
     *
     * @param player       player to open menu for
     * @param menuProvider menu factory
     * @param dataWriter   additional data added via {@link RegistryFriendlyByteBuf}
     */
    void openMenu(ServerPlayer player, MenuProvider menuProvider, BiConsumer<ServerPlayer, RegistryFriendlyByteBuf> dataWriter);

    /**
     * Get the parent mob from a possible mob part entity, like
     * {@link net.minecraft.world.entity.boss.EnderDragonPart}.
     * <p>
     * NeoForge allows extending this, so we need this abstraction.
     *
     * @param entity the mob, possibly a mob part
     * @return the parent mob for the part, otherwise the original entity
     */
    Entity getPartEntityParent(Entity entity);

    /**
     * Is the entity type considered a boss mob like {@link EntityType#ENDER_DRAGON} and {@link EntityType#WITHER} in
     * vanilla.
     * <p>
     * A common property of such entities usually is {@link Entity#canUsePortal(boolean)} returning
     * <code>false</code>.
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
    boolean canEquip(ItemStack stack, EquipmentSlot slot, LivingEntity entity);

    /**
     * Called before an entity drops loot for determining the level of
     * {@link net.minecraft.world.item.enchantment.Enchantments#LOOTING} to apply when generating drops.
     *
     * @param target       the entity that has been killed
     * @param attacker     another entity responsible for killing the entity
     * @param damageSource the damage source the entity has been killed by
     * @return the level of looting to apply when generating drops
     */
    default int getMobLootingLevel(Entity target, @Nullable Entity attacker, @Nullable DamageSource damageSource) {
        if (attacker instanceof LivingEntity livingEntity) {
            Holder<Enchantment> enchantment = LookupHelper.lookupEnchantment(target, Enchantments.LOOTING);
            return EnchantmentHelper.getEnchantmentLevel(enchantment, livingEntity);
        } else {
            return 0;
        }
    }

    /**
     * Called when a <code>mobGriefing</code> game rule check is required instead of vanilla's
     * <code>level.getGameRules().getBoolean(GameRules.RULE_MOBGRIEFING)</code>, allowing for a dedicated Forge event
     * to run.
     *
     * @param serverLevel the level mob griefing is happening in
     * @param entity      the entity responsible for triggering the game rule check
     * @return is mob griefing allows to happen
     */
    boolean getMobGriefingRule(ServerLevel serverLevel, @Nullable Entity entity);

    /**
     * A trigger for running a Forge event for destroying an item.
     *
     * @param player            the player destroying the item
     * @param originalItemStack the item stack before being destroyed
     * @param interactionHand   the hand holding the destroyed stack
     */
    void onPlayerDestroyItem(Player player, ItemStack originalItemStack, @Nullable InteractionHand interactionHand);

    /**
     * Retrieves a {@link EntitySpawnReason} from a {@link Mob} if it has been set during
     * {@link Mob#finalizeSpawn(ServerLevelAccessor, DifficultyInstance, EntitySpawnReason, SpawnGroupData)}.
     * <p>Note that the spawn type is saved with the mob, so it persists across chunk and level reloads.
     *
     * @param mob the mob
     * @return the spawn type or null if none has been set
     */
    @Nullable EntitySpawnReason getMobSpawnType(Mob mob);

    /**
     * Creates a new {@link Pack.Metadata} instance with additional parameters only supported on NeoForge.
     *
     * @param id                the pack identifier
     * @param description       the pack description component
     * @param packCompatibility the pack version, ideally retrieved from
     *                          {@link net.minecraft.WorldVersion#getPackVersion(PackType)}
     * @param features          the feature flags provided by this pack
     * @param hidden            controls whether the pack is hidden from user-facing screens like the resource pack and
     *                          data pack selection screens
     * @return the created pack info instance
     */
    Pack.Metadata createPackInfo(ResourceLocation id, Component description, PackCompatibility packCompatibility, FeatureFlagSet features, boolean hidden);

    /**
     * Can the given enchanted be applied to an item stack via enchanting (in an enchanting table).
     *
     * @param enchantment the enchantment to check
     * @param itemStack   the item stack trying to receive the enchantment
     * @return is the application allowed
     */
    boolean canApplyAtEnchantingTable(Holder<Enchantment> enchantment, ItemStack itemStack);

    /**
     * Can the given enchantment be applied to enchanted books.
     *
     * @param enchantment the enchantment to check
     * @return is the application allowed
     */
    default boolean isAllowedOnBooks(Holder<Enchantment> enchantment) {
        return true;
    }

    /**
     * Tests if an enchanted book can be put onto an item stack.
     *
     * @param inputStack the item stack to enchant
     * @param bookStack  the book stack to enchant the item with
     * @return is combining both stacks allowed
     */
    boolean isBookEnchantable(ItemStack inputStack, ItemStack bookStack);

    /**
     * Called just before an {@link Explosion} is about to be executed for a level.
     *
     * @param serverLevel the level the explosion is happening in
     * @param explosion   the explosion that is about to start
     * @return <code>true</code> to mark the explosion as handled, {@link ServerExplosion#explode()} is not called
     */
    boolean onExplosionStart(ServerLevel serverLevel, ServerExplosion explosion);
}
