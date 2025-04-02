package fuzs.puzzleslib.api.client.gui.v2.tooltip;

import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.ItemLike;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * A helper class for registering item tooltips for items and blocks.
 */
public final class ItemTooltipRegistry {

    private ItemTooltipRegistry() {
        // NO-OP
    }

    /**
     * Registers an item tooltip provider built from a component.
     *
     * @param clazz     the item / block class
     * @param component the component
     * @param <T>       the item / block type
     */
    public static <T extends ItemLike> void registerItemTooltip(Class<T> clazz, Component component) {
        registerItemTooltip(clazz, (T t) -> component);
    }

    /**
     * Registers an item tooltip provider built from a component.
     *
     * @param itemLike  the item / block
     * @param component the component
     * @param <T>       the item / block type
     */
    public static <T extends ItemLike> void registerItemTooltip(T itemLike, Component component) {
        registerItemTooltip(itemLike, (T t) -> component);
    }

    /**
     * Registers an item tooltip provider built from extracting a component.
     *
     * @param clazz              the item / block class
     * @param componentExtractor the component getter from the item / block
     * @param <T>                the item / block type
     */
    public static <T extends ItemLike> void registerItemTooltip(Class<T> clazz, Function<T, Component> componentExtractor) {
        registerItemTooltip(clazz,
                (T t, ItemStack itemStack, Item.TooltipContext context, TooltipFlag tooltipFlag, Consumer<Component> tooltipLineConsumer) -> {
                    tooltipLineConsumer.accept(componentExtractor.apply(t));
                });
    }

    /**
     * Registers an item tooltip provider built from extracting a component.
     *
     * @param itemLike           the item / block
     * @param componentExtractor the component getter from the item / block
     * @param <T>                the item / block type
     */
    public static <T extends ItemLike> void registerItemTooltip(T itemLike, Function<T, Component> componentExtractor) {
        registerItemTooltip(itemLike,
                (T t, ItemStack itemStack, Item.TooltipContext context, TooltipFlag tooltipFlag, Consumer<Component> tooltipLineConsumer) -> {
                    tooltipLineConsumer.accept(componentExtractor.apply(t));
                });
    }

    /**
     * Registers an item tooltip provider.
     *
     * @param clazz    the item / block class
     * @param provider the tooltip provider
     * @param <T>      the item / block type
     */
    @SuppressWarnings("unchecked")
    public static <T extends ItemLike> void registerItemTooltip(Class<T> clazz, Provider<T> provider) {
        for (Block block : BuiltInRegistries.BLOCK) {
            if (clazz.isInstance(block)) {
                registerItemTooltip((T) block, provider);
            }
        }
        for (Item item : BuiltInRegistries.ITEM) {
            if (clazz.isInstance(item)) {
                registerItemTooltip((T) item, provider);
            }
        }
    }

    /**
     * Registers an item tooltip provider.
     *
     * @param itemLike the item / block
     * @param provider the tooltip provider
     * @param <T>      the item / block type
     */
    public static <T extends ItemLike> void registerItemTooltip(T itemLike, Provider<T> provider) {
        ItemTooltipCallback.EVENT.register((ItemStack itemStack, List<Component> tooltipLines, Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag) -> {
            if (itemStack.getItem() == itemLike ||
                    itemStack.getItem() instanceof BlockItem blockItem && blockItem.getBlock() == itemLike) {
                int originalSize = tooltipLines.size();
                provider.appendHoverText(itemLike, itemStack, tooltipContext, tooltipFlag, (Component component) -> {
                    // add lines directly below the item name
                    tooltipLines.addAll(tooltipLines.isEmpty() ? 0 : 1 + tooltipLines.size() - originalSize,
                            ClientComponentSplitter.splitTooltipComponents(component));
                });
            }
        });
    }

    @FunctionalInterface
    public interface Provider<T extends ItemLike> {

        /**
         * A tooltip provider for adding individual tooltip lines.
         *
         * @param itemLike            the item / block
         * @param itemStack           the item stack
         * @param tooltipContext      the tooltip item context
         * @param tooltipFlag         the tooltip flag context
         * @param tooltipLineConsumer the tooltip line adder
         */
        void appendHoverText(T itemLike, ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, Consumer<Component> tooltipLineConsumer);
    }
}
