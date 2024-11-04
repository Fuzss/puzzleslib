package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent;

import java.util.Objects;

public record CreativeTabContentsContextNeoForgeImpl(BuildCreativeModeTabContentsEvent evt) implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceKey<CreativeModeTab> resourceKey, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        if (resourceKey == this.evt.getTabKey()) {
            itemsGenerator.accept(this.evt.getParameters(), this.evt);
        }
    }
}
