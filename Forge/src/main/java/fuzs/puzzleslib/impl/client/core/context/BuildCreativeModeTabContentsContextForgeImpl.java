package fuzs.puzzleslib.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.BuildCreativeModeTabContentsContext;
import fuzs.puzzleslib.api.item.v2.DisplayItemsOutput;
import fuzs.puzzleslib.impl.client.core.event.CreativeModeTabContentsEvent;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.common.MinecraftForge;

import java.util.Objects;
import java.util.function.Consumer;

public record BuildCreativeModeTabContentsContextForgeImpl() implements BuildCreativeModeTabContentsContext {

    @Override
    public void registerBuildListener(ResourceLocation identifier, Consumer<DisplayItemsOutput> itemsGenerator) {
        Objects.requireNonNull(identifier, "identifier is null");
        Objects.requireNonNull(itemsGenerator, "display items generator is null");
        MinecraftForge.EVENT_BUS.addListener((final CreativeModeTabContentsEvent evt) -> {
            if (Objects.equals(identifier, BuildCreativeModeTabContentsContext.tryCreateIdentifier(evt.getTab()))) {
                itemsGenerator.accept(evt.getOutput());
            }
        });
    }
}
