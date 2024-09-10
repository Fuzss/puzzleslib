package fuzs.puzzleslib.api.block.v1.entity;

import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BaseContainerBlockEntity;
import net.minecraft.world.level.block.entity.BlockEntity;

import java.util.function.BiConsumer;

/**
 * A helper class containing {@link BlockEntity} related methods.
 */
public final class BlockEntityHelper {

    private BlockEntityHelper() {
        // NO-OP
    }

    /**
     * Copies the name of an item stack to a block entity after placing the item stack into the world.
     * <p>
     * Used as a compatibility layer for older Minecraft versions, since Minecraft 1.20.5+ this is handled via item
     * components by the block entity itself.
     *
     * @param itemStack the item stack to copy the block entity name from
     * @param level     the current level
     * @param pos       the position of the block entity
     * @param clazz     the type of block entity
     * @param <T>       the type of block entity
     */
    public static <T extends BaseContainerBlockEntity> void setCustomName(ItemStack itemStack, Level level, BlockPos pos, Class<T> clazz) {
        // NO-OP
    }

    /**
     * Copies the name of an item stack to a block entity after placing the item stack into the world.
     * <p>
     * Used as a compatibility layer for older Minecraft versions, since Minecraft 1.20.5+ this is handled via item
     * components by the block entity itself.
     *
     * @param itemStack        the item stack to copy the block entity name from
     * @param level            the current level
     * @param pos              the position of the block entity
     * @param clazz            the type of block entity
     * @param customNameSetter set the item stack custom name to the block entity
     * @param <T>              the type of block entity
     */
    public static <T extends BlockEntity> void setCustomName(ItemStack itemStack, Level level, BlockPos pos, Class<T> clazz, BiConsumer<T, Component> customNameSetter) {
        // NO-OP
    }
}
