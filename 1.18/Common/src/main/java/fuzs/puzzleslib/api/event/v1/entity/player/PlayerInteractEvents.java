package fuzs.puzzleslib.api.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResultHolder;
import net.minecraft.core.BlockPos;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.MenuProvider;
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
    public static final EventInvoker<UseItem> USE_ITEM = EventInvoker.lookup(UseItem.class);
    public static final EventInvoker<UseEntity> USE_ENTITY = EventInvoker.lookup(UseEntity.class);
    public static final EventInvoker<UseEntityAt> USE_ENTITY_AT = EventInvoker.lookup(UseEntityAt.class);

    private PlayerInteractEvents() {

    }

    @FunctionalInterface
    public interface UseBlock {

        /**
         * This event is fired on both sides before the player triggers {@link net.minecraft.world.level.block.Block#use(BlockState, Level, BlockPos, Player, InteractionHand, BlockHitResult)} by right-clicking.
         *
         * @param player    the player interacting with the block
         * @param level     the level the interaction is happening in
         * @param hand      the hand <code>player</code> is using to interact
         * @param hitResult the targeted block
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         * {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResult> onUseBlock(Player player, Level level, InteractionHand hand, BlockHitResult hitResult);
    }

    @FunctionalInterface
    public interface UseItem {

        /**
         * This event is fired on both sides before the player triggers {@link Item#use(Level, Player, InteractionHand)} by right-clicking an item.
         *
         * @param player the player using the item
         * @param level  the level the interaction is happening in
         * @param hand   the hand <code>player</code> is holding the item
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         * {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResultHolder<ItemStack>> onUseItem(Player player, Level level, InteractionHand hand);
    }

    @FunctionalInterface
    public interface UseEntity {

        /**
         * Called when the player clicks an entity, runs on the player first, then proceeds to calling {@link Entity#interact(Player, InteractionHand)}.
         * <p>This type of interaction also allows for opening menus via an entity implementing {@link MenuProvider}.
         *
         * @param player the player interacting with the entity
         * @param level  the level the interaction is happening in
         * @param hand   the hand <code>player</code> is using to interact
         * @param entity the entity this interaction is happening on
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         * {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResult> onUseEntity(Player player, Level level, InteractionHand hand, Entity entity);
    }

    @FunctionalInterface
    public interface UseEntityAt {

        /**
         * Called when the player clicks an entity, this interaction includes the exact position where the player clicked on the entity's bounding box.
         * <p>For this interaction {@link Entity#interactAt(Player, Vec3, InteractionHand)} is called, if it returns {@link InteractionResult#PASS} more interactions are tried.
         *
         * @param player    the player interacting with the entity
         * @param level     the level the interaction is happening in
         * @param hand      the hand <code>player</code> is using to interact
         * @param entity    the entity this interaction is happening on
         * @param hitVector the exact position where the player has clicked on the entity's bounding box
         * @return {@link EventResultHolder#interrupt(Object)} to cancel vanilla interaction, returning the instance supplier to the holder instead
         * {@link EventResultHolder#pass()} to allow the vanilla behavior for this interaction to proceed
         */
        EventResultHolder<InteractionResult> onUseEntityAt(Player player, Level level, InteractionHand hand, Entity entity, Vec3 hitVector);
    }
}
