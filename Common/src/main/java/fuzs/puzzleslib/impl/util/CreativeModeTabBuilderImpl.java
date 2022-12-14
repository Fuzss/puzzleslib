package fuzs.puzzleslib.impl.util;

import fuzs.puzzleslib.util.CreativeModeTabBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.List;
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
    BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay;
    boolean showSearch;

    public CreativeModeTabBuilderImpl(String modId, String identifier) {
        this.identifier = new ResourceLocation(modId, identifier);
    }

    ItemStack getIcon() {
        ItemStack stack = this.iconSupplier.get();
        if (stack.isEmpty()) throw new IllegalStateException("creative tab icon cannot be empty");
        return stack;
    }

    @Override
    public CreativeModeTabBuilder setIcon(Supplier<ItemStack> icon) {
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
    public CreativeModeTabBuilder appendItems(BiConsumer<List<ItemStack>, CreativeModeTab> stacksForDisplay) {
        this.stacksForDisplay = stacksForDisplay;
        return this;
    }

    @Override
    public CreativeModeTabBuilder showSearch() {
        this.showSearch = true;
        return this;
    }
}
