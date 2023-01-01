package fuzs.puzzleslib.impl.creativetab;

import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.Supplier;

public abstract class CreativeModeTabBuilderImpl implements CreativeModeTabBuilder {
    final ResourceLocation identifier;
    private Supplier<ItemStack> iconSupplier = () -> ItemStack.EMPTY;
    boolean cacheIcon = true;
    boolean showTitle = true;
    boolean showScrollbar = true;
    boolean alignRight;
    @Nullable
    BiConsumer<NonNullList<ItemStack>, CreativeModeTab> stacksForDisplay;
    boolean showSearch;

    public CreativeModeTabBuilderImpl(String modId, String identifier) {
        this.identifier = new ResourceLocation(modId, identifier);
    }

    ItemStack getIcon() {
        ItemStack stack = this.iconSupplier.get();
        if (stack.isEmpty()) throw new IllegalStateException("Creative tab icon cannot be empty");
        return stack;
    }

    @Override
    public CreativeModeTabBuilder setIcon(Supplier<ItemStack> icon) {
        Objects.requireNonNull(icon, "icon supplier was null");
        this.iconSupplier = icon;
        return this;
    }

    @Override
    public CreativeModeTabBuilder disableIconCache() {
        this.cacheIcon = false;
        return this;
    }

    @Override
    public CreativeModeTabBuilder hideTitle() {
        this.showTitle = false;
        return this;
    }

    @Override
    public CreativeModeTabBuilder hideScroll() {
        this.showScrollbar = false;
        return this;
    }

    @Override
    public CreativeModeTabBuilder alignRight() {
        this.alignRight = true;
        return this;
    }

    @Override
    public CreativeModeTabBuilder appendItemsV2(BiConsumer<NonNullList<ItemStack>, CreativeModeTab> stacksForDisplay) {
        Objects.requireNonNull(stacksForDisplay, "stacks for display consumer was null");
        this.stacksForDisplay = stacksForDisplay;
        return this;
    }

    @Override
    public CreativeModeTabBuilder showSearch() {
        this.showSearch = true;
        return this;
    }
}
