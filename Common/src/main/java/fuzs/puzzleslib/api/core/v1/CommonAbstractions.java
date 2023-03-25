package fuzs.puzzleslib.api.core.v1;

import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

import java.util.function.BiConsumer;

/**
 * useful methods for gameplay related things that require mod loader specific abstractions
 */
public interface CommonAbstractions {
    /**
     * instance of the common abstractions SPI
     */
    CommonAbstractions INSTANCE = ServiceProviderHelper.load(CommonAbstractions.class);

    /**
     * opens a menu on both client and server
     *
     * @param player       player to open menu for
     * @param menuProvider menu factory
     */
    default void openMenu(ServerPlayer player, MenuProvider menuProvider) {
        this.openMenu(player, menuProvider, (ServerPlayer serverPlayer, FriendlyByteBuf buf) -> {

        });
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
}
