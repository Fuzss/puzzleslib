package fuzs.puzzleslib.neoforge.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.BuildCreativeModeTabContentsContext;
import net.minecraft.resources.ResourceKey;
import net.minecraft.world.item.CreativeModeTab;

import java.util.Objects;

public record BuildCreativeModeTabContentsContextForgeImpl(ResourceKey<CreativeModeTab> resourceKey,
                                                           CreativeModeTab.ItemDisplayParameters parameters,
                                                           CreativeModeTab.Output output) implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceKey<CreativeModeTab> resourceKey, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(resourceKey, "resource key is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        if (resourceKey == this.resourceKey) itemsGenerator.accept(this.parameters, this.output);
    }
}
