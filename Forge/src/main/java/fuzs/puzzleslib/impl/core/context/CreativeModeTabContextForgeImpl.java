package fuzs.puzzleslib.impl.core.context;

import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import org.jetbrains.annotations.Nullable;

import java.util.function.BiConsumer;
import java.util.function.Consumer;

public record CreativeModeTabContextForgeImpl(
        BiConsumer<ResourceLocation, Consumer<CreativeModeTab.Builder>> consumer) implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTabConfiguratorImpl configuratorImpl = (CreativeModeTabConfiguratorImpl) configurator;
        this.consumer.accept(configuratorImpl.getIdentifier(), (CreativeModeTab.Builder builder) -> {
            this.finalizeCreativeModeTabBuilder(builder, configuratorImpl);
        });
    }

    private void finalizeCreativeModeTabBuilder(CreativeModeTab.Builder builder, CreativeModeTabConfiguratorImpl configuratorImpl) {
        configuratorImpl.configure(builder);
        String translationKey = "itemGroup.%s.%s".formatted(configuratorImpl.getIdentifier().getNamespace(), configuratorImpl.getIdentifier().getPath());
        builder.title(Component.translatable(translationKey));
        if (configuratorImpl.isHasSearchBar()) {
            builder.withSearchBar();
        }
        if (configuratorImpl.getIcons() != null) {
            builder.withTabFactory(other -> new CreativeModeTab(other) {
                @Nullable
                private ItemStack[] itemStacks;

                @Override
                public ItemStack getIconItem() {
                    // stolen from XFactHD, thanks :)
                    if (this.itemStacks == null) {
                        this.itemStacks = configuratorImpl.getIcons().get();
                    }
                    int index = (int) (System.currentTimeMillis() / 2000) % this.itemStacks.length;
                    return this.itemStacks[index];
                }
            });
        }
    }
}
