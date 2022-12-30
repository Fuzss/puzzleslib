package fuzs.puzzleslib.impl.creativetab;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.world.item.CreativeModeTab;

public class FabricCreativeModeTabBuilder extends CreativeModeTabBuilderImpl {

    public FabricCreativeModeTabBuilder(String modId, String identifier) {
        super(modId, identifier);
    }

    @SuppressWarnings("Convert2MethodRef")
    @Override
    public CreativeModeTab build() {
        // better not make this method reference, could result in early loading issue
        FabricItemGroupBuilder builder = FabricItemGroupBuilder.create(this.identifier).icon(() -> this.getIcon());
        if (this.stacksForDisplay != null) builder.appendItems(this.stacksForDisplay);
        return builder.build();
    }
}
