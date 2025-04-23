package fuzs.puzzleslib.api.client.gui.v2.tooltip;

import fuzs.puzzleslib.api.client.event.v1.gui.ItemTooltipCallback;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.block.Block;
import org.jetbrains.annotations.Nullable;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

/**
 * A helper class for registering item tooltips for items and blocks.
 */
public abstract class ItemTooltipRegistryV2<T> {
    /**
     * The block registry.
     */
    public static final ItemTooltipRegistryV2<Block> BLOCK = new ItemTooltipRegistryV2<>() {
        @Override
        @Nullable Block getFromItemStack(ItemStack itemStack) {
            return itemStack.getItem() instanceof BlockItem blockItem ? blockItem.getBlock() : null;
        }

        @Override
        Registry<Block> getRegistry() {
            return BuiltInRegistries.BLOCK;
        }
    };
    /**
     * The item registry.
     */
    public static final ItemTooltipRegistryV2<Item> ITEM = new ItemTooltipRegistryV2<>() {
        @Override
        Item getFromItemStack(ItemStack itemStack) {
            return itemStack.getItem();
        }

        @Override
        Registry<Item> getRegistry() {
            return BuiltInRegistries.ITEM;
        }
    };

    ItemTooltipRegistryV2() {
        // NO-OP
    }

    abstract @Nullable T getFromItemStack(ItemStack itemStack);

    abstract Registry<T> getRegistry();

    /**
     * Register an item tooltip provider built from a component.
     *
     * @param value     the item / block
     * @param component the component
     */
    public void registerItemTooltip(T value, Component component) {
        this.registerItemTooltip(value, new Component[]{component});
    }

    /**
     * Register an item tooltip provider built from a component.
     *
     * @param clazz     the item / block class
     * @param component the component
     */
    public void registerItemTooltip(Class<T> clazz, Component component) {
        this.registerItemTooltip(clazz, new Component[]{component});
    }

    /**
     * Register an item tooltip provider built from a component.
     *
     * @param tagKey    the item / block tag key
     * @param component the components
     */
    public void registerItemTooltip(TagKey<T> tagKey, Component component) {
        this.registerItemTooltip(tagKey, new Component[]{component});
    }

    /**
     * Register an item tooltip provider built from components.
     *
     * @param value      the item / block
     * @param components the component
     */
    public void registerItemTooltip(T value, Component... components) {
        this.registerItemTooltipLines(value, (T valueX) -> Arrays.asList(components));
    }

    /**
     * Register an item tooltip provider built from components.
     *
     * @param clazz      the item / block class
     * @param components the component
     */
    public void registerItemTooltip(Class<T> clazz, Component... components) {
        this.registerItemTooltipLines(clazz, (T valueX) -> Arrays.asList(components));
    }

    /**
     * Register an item tooltip provider built from components.
     *
     * @param tagKey     the item / block tag key
     * @param components the components
     */
    public void registerItemTooltip(TagKey<T> tagKey, Component... components) {
        this.registerItemTooltipLines(tagKey, (T valueX) -> Arrays.asList(components));
    }

    /**
     * Register an item tooltip provider built from extracting a component.
     *
     * @param value              the item / block
     * @param componentExtractor the component getter from the item / block
     */
    public void registerItemTooltip(T value, Function<T, Component> componentExtractor) {
        this.registerItemTooltipLines(value, (T valueX) -> {
            return Collections.singletonList(componentExtractor.apply(valueX));
        });
    }

    /**
     * Register an item tooltip provider built from extracting a component.
     *
     * @param clazz              the item / block class
     * @param componentExtractor the component getter from the item / block
     */
    public void registerItemTooltip(Class<T> clazz, Function<T, Component> componentExtractor) {
        this.registerItemTooltipLines(clazz, (T valueX) -> {
            return Collections.singletonList(componentExtractor.apply(valueX));
        });
    }

    /**
     * Register an item tooltip provider built from extracting a component.
     *
     * @param tagKey             the item / block tag key
     * @param componentExtractor the component getter from the item / block
     */
    public void registerItemTooltip(TagKey<T> tagKey, Function<T, Component> componentExtractor) {
        this.registerItemTooltipLines(tagKey, (T valueX) -> {
            return Collections.singletonList(componentExtractor.apply(valueX));
        });
    }

    /**
     * Register an item tooltip provider built from extracting components.
     *
     * @param value              the item / block
     * @param componentExtractor the component getter from the item / block
     */
    public void registerItemTooltipLines(T value, Function<T, List<Component>> componentExtractor) {
        registerItemTooltip((ItemStack itemStack) -> this.getFromItemStack(itemStack) == value,
                (ItemStack itemStack, Item.TooltipContext context, TooltipFlag tooltipFlag, @Nullable Player player, Consumer<Component> tooltipLineConsumer) -> {
                    componentExtractor.apply(value).forEach(tooltipLineConsumer);
                });
    }

    /**
     * Register an item tooltip provider built from extracting components.
     *
     * @param clazz              the item / block class
     * @param componentExtractor the component getter from the item / block
     */
    public void registerItemTooltipLines(Class<T> clazz, Function<T, List<Component>> componentExtractor) {
        this.registerItemTooltip(clazz,
                (ItemStack itemStack, Item.TooltipContext context, TooltipFlag tooltipFlag, @Nullable Player player, Consumer<Component> tooltipLineConsumer) -> {
                    T value = this.getFromItemStack(itemStack);
                    Objects.requireNonNull(value, "value from item stack " + itemStack + " is null");
                    componentExtractor.apply(value).forEach(tooltipLineConsumer);
                });
    }

    /**
     * Register an item tooltip provider built from extracting components.
     *
     * @param tagKey             the item / block tag key
     * @param componentExtractor the component getter from the item / block
     */
    public void registerItemTooltipLines(TagKey<T> tagKey, Function<T, List<Component>> componentExtractor) {
        this.registerItemTooltip(tagKey,
                (ItemStack itemStack, Item.TooltipContext context, TooltipFlag tooltipFlag, @Nullable Player player, Consumer<Component> tooltipLineConsumer) -> {
                    T value = this.getFromItemStack(itemStack);
                    Objects.requireNonNull(value, "value from item stack " + itemStack + " is null");
                    componentExtractor.apply(value).forEach(tooltipLineConsumer);
                });
    }

    /**
     * Register an item tooltip provider.
     *
     * @param clazz    the item / block class
     * @param provider the tooltip provider
     */
    public void registerItemTooltip(Class<T> clazz, Provider provider) {
        for (T value : this.getRegistry()) {
            if (clazz.isInstance(value)) {
                registerItemTooltip((ItemStack itemStack) -> {
                    return this.getFromItemStack(itemStack) == value;
                }, provider);
            }
        }
    }

    /**
     * Register an item tooltip provider.
     *
     * @param tagKey   the item / block tag key
     * @param provider the tooltip provider
     */
    public void registerItemTooltip(TagKey<T> tagKey, Provider provider) {
        registerItemTooltip((ItemStack itemStack) -> {
            T value = this.getFromItemStack(itemStack);
            return value != null && this.getRegistry().wrapAsHolder(value).is(tagKey);
        }, provider);
    }

    /**
     * Register an item tooltip provider.
     *
     * @param itemStackFilter the filter for the valid item / block
     * @param provider        the tooltip provider
     */
    public static void registerItemTooltip(Predicate<ItemStack> itemStackFilter, Provider provider) {
        ItemTooltipCallback.EVENT.register((ItemStack itemStack, List<Component> tooltipLines, Item.TooltipContext tooltipContext, @Nullable Player player, TooltipFlag tooltipFlag) -> {
            if (tooltipContext != Item.TooltipContext.EMPTY && itemStackFilter.test(itemStack)) {
                int originalSize = tooltipLines.size();
                provider.appendHoverText(itemStack, tooltipContext, tooltipFlag, player, (Component component) -> {
                    // add lines directly below the item name
                    tooltipLines.addAll(tooltipLines.isEmpty() ? 0 : 1 + tooltipLines.size() - originalSize,
                            ClientComponentSplitter.splitTooltipComponents(component));
                });
            }
        });
    }

    @FunctionalInterface
    public interface Provider {

        /**
         * A tooltip provider for adding individual tooltip lines.
         *
         * @param itemStack           the item stack
         * @param tooltipContext      the tooltip item context
         * @param tooltipFlag         the tooltip flag context
         * @param player              the player viewing the tooltip
         * @param tooltipLineConsumer the tooltip line adder
         */
        void appendHoverText(ItemStack itemStack, Item.TooltipContext tooltipContext, TooltipFlag tooltipFlag, @Nullable Player player, Consumer<Component> tooltipLineConsumer);
    }
}
