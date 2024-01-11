package fuzs.puzzleslib.impl.creativetab;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.core.Registry;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;
import java.util.function.BiConsumer;

public class FabricCreativeModeTabBuilder extends CreativeModeTabBuilderImpl {
    private static final BiConsumer<NonNullList<ItemStack>, CreativeModeTab> DEFAULT_ITEM_APPENDER = (NonNullList<ItemStack> itemStacks, CreativeModeTab creativeModeTab) -> {
        for (Item item : Registry.ITEM) {
            item.fillItemCategory(creativeModeTab, itemStacks);
        };
    };

    public FabricCreativeModeTabBuilder(String modId, String identifier) {
        super(modId, identifier);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public CreativeModeTab build() {
        // better not make this method reference, could result in early loading issue
        FabricItemGroupBuilder builder = FabricItemGroupBuilder.create(this.identifier).icon(() -> this.getIcon());
        if (this.stacksForDisplay != null || this.appendEnchantments || this.appendPotions) {
            builder.appendItems((itemStacks, creativeModeTab) -> {
                BiConsumer<NonNullList<ItemStack>, CreativeModeTab> itemAppender = this.stacksForDisplay == null ? DEFAULT_ITEM_APPENDER : this.stacksForDisplay;
                NonNullList<ItemStack> nonNullList = toNonNullList(itemStacks);
                itemAppender.accept(nonNullList, creativeModeTab);
                this.appendAdditionals(nonNullList);
            });
        }
        return builder.build();
    }

    private static <E> NonNullList<E> toNonNullList(List<E> list) {
        if (list instanceof NonNullList<E>) return (NonNullList<E>) list;
        return new NonNullList<>(list, null) {

        };
    }
}
