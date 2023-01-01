package fuzs.puzzleslib.impl.creativetab;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.core.NonNullList;
import net.minecraft.world.item.CreativeModeTab;

import java.util.List;

public class FabricCreativeModeTabBuilder extends CreativeModeTabBuilderImpl {

    public FabricCreativeModeTabBuilder(String modId, String identifier) {
        super(modId, identifier);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public CreativeModeTab build() {
        // better not make this method reference, could result in early loading issue
        FabricItemGroupBuilder builder = FabricItemGroupBuilder.create(this.identifier).icon(() -> this.getIcon());
        if (this.stacksForDisplay != null) builder.appendItems((itemStacks, creativeModeTab) -> {
            this.stacksForDisplay.accept(toNonNullList(itemStacks), creativeModeTab);
        });
        return builder.build();
    }

    private static <E> NonNullList<E> toNonNullList(List<E> list) {
        if (list instanceof NonNullList<E>) return (NonNullList<E>) list;
        return new NonNullList<>(list, null) {

        };
    }
}
