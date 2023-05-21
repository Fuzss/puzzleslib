package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuildCreativeModeTabContentsContext;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraftforge.common.CreativeModeTabRegistry;

import java.util.Objects;

public record BuildCreativeModeTabContentsContextForgeImpl(ResourceLocation identifier,
                                                           CreativeModeTab.ItemDisplayParameters parameters,
                                                           CreativeModeTab.Output output) implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceLocation identifier, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        if (Objects.equals(identifier, this.identifier)) {
            itemsGenerator.accept(this.parameters, this.output);
        }
    }

    @Override
    public void registerBuildListener(CreativeModeTab tab, CreativeModeTab.DisplayItemsGenerator itemsGenerator) {
        Objects.requireNonNull(tab, "creative mode tab is null");
        ResourceLocation identifier = CreativeModeTabRegistry.getName(tab);
        this.registerBuildListener(identifier, itemsGenerator);
    }
}
