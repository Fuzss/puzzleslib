package fuzs.puzzleslib.common.api.event.v1.entity.player;

import fuzs.puzzleslib.common.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.common.api.event.v1.core.EventResult;
import fuzs.puzzleslib.common.api.event.v1.core.EventResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.BlockHitResult;
import net.minecraft.world.phys.Vec3;

public final class PlayerInteractEvents {
    public static final EventInvoker<UseBlock> USE_BLOCK = EventInvoker.lookup(UseBlock.class);
    public static final EventInvoker<AttackBlock> ATTACK_BLOCK = EventInvoker.lookup(AttackBlock.class);
    public static final EventInvoker<UseItem> USE_ITEM = EventInvoker.lookup(UseItem.class);
    public static final EventInvoker<UseEntity> USE_ENTITY = EventInvoker.lookup(UseEntity.class);
    public static final EventInvoker<AttackEntity> ATTACK_ENTITY = EventInvoker.lookup(AttackEntity.class);

    private PlayerInteractEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface UseBlock {

        /**
         * This event is fired on both sides before the player triggers
         * {@link net.minecraft.world.level.block.Block#useItemOn(ItemStack, BlockState, Level, BlockPos, Player,
         * InteractionHand, BlockHitResult)} by right-clicking.
         *
         * @param player          the player interacting with the block
         * @param level           the level the interaction is happening in
         * @param interactionHand the hand the player is using to interact
         * @param hitResult       the targeted block
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead</li>
         *         <li>{@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed</li>
         *         </ul>
         */
        EventResultHolder<InteractionResult> onUseBlock(Player player, Level level, InteractionHand interactionHand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    public interface AttackBlock {

        /**
         * This event is fired on both sides before the player triggers
         * {@link net.minecraft.world.level.block.Block#attack(BlockState, Level, BlockPos, Player)} by left-clicking.
         *
         * @param player          the player interacting with the block
         * @param level           the level the interaction is happening in
         * @param interactionHand the hand the player is using to interact
         * @param blockPos        the position of the block in the <code>level</code>
         * @param direction       the direction the block is clicked at
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the player from beginning to mine the block</li>
         *         <li>{@link EventResult#PASS PASS} to allow the vanilla behavior for this interaction to proceed</li>
         *         </ul>
         */
        EventResult onAttackBlock(Player player, Level level, InteractionHand interactionHand, BlockPos blockPos, Direction direction);
    }

    @FunctionalInterface
    public interface UseItem {

        /**
         * This event is fired on both sides before the player triggers {@link Item#use(Level, Player, InteractionHand)}
         * by right-clicking an item.
         *
         * @param player          the player using the item
         * @param level           the level the interaction is happening in
         * @param interactionHand the hand the player is holding the item
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead</li>
         *         <li>{@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed</li>
         *         </ul>
         */
        EventResultHolder<InteractionResult> onUseItem(Player player, Level level, InteractionHand interactionHand);
    }

    @FunctionalInterface
    public interface UseEntity {

        /**
         * Called when the player clicks an entity, runs on the player first, then proceeds to calling
         * {@link Entity#interact(Player, InteractionHand, Vec3)}.
         *
         * @param player          the player interacting with the entity
         * @param level           the level the interaction is happening in
         * @param interactionHand the hand the player is using to interact
         * @param entity          the entity this interaction is happening on
         * @param hitVector       the exact position where the player has clicked on the entity's bounding box
         * @return <ul>
         *         <li>{@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead</li>
         *         <li>{@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed</li>
         *         </ul>
         */
        EventResultHolder<InteractionResult> onUseEntity(Player player, Level level, InteractionHand interactionHand, Entity entity, Vec3 hitVector);
    }

    @FunctionalInterface
    public interface AttackEntity {

        /**
         * Called before a player attacks another entity in {@link Player#attack(Entity)}. Allows for preventing the
         * attack.
         *
         * @param player          the player that is attacking
         * @param level           the level the interaction is happening in
         * @param interactionHand the hand the player is using to interact
         * @param entity          the entity under attack
         * @return <ul>
         *         <li>{@link EventResult#INTERRUPT INTERRUPT} to prevent the entity from being attacked, allows for processing different actions on hitting an entity</li>
         *         <li>{@link EventResult#PASS PASS} to allow the entity to be attack</li>
         *         </ul>
         */
        EventResult onAttackEntity(Player player, Level level, InteractionHand interactionHand, Entity entity);
    }
}
