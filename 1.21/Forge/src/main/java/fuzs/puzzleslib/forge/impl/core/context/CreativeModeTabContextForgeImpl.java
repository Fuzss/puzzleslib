package fuzs.puzzleslib.forge.impl.core.context;

import com.google.common.base.Preconditions;
import fuzs.puzzleslib.api.core.v1.context.CreativeModeTabContext;
import fuzs.puzzleslib.api.item.v2.CreativeModeTabConfigurator;
import fuzs.puzzleslib.impl.item.CreativeModeTabConfiguratorImpl;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.registries.DeferredRegister;
import org.jetbrains.annotations.Nullable;

import java.util.Objects;

public record CreativeModeTabContextForgeImpl(IEventBus modEventBus) implements CreativeModeTabContext {

    @Override
    public void registerCreativeModeTab(CreativeModeTabConfigurator configurator) {
        CreativeModeTabConfiguratorImpl configuratorImpl = (CreativeModeTabConfiguratorImpl) configurator;
        DeferredRegister<CreativeModeTab> deferredRegister = DeferredRegister.create(Registries.CREATIVE_MODE_TAB,
                configuratorImpl.getIdentifier().getNamespace()
        );
        deferredRegister.register(this.modEventBus);
        CreativeModeTab.Builder builder = CreativeModeTab.builder();
        this.finalizeCreativeModeTabBuilder(builder, configuratorImpl);
        deferredRegister.register(configuratorImpl.getIdentifier().getPath(), builder::build);
    }

    private void finalizeCreativeModeTabBuilder(CreativeModeTab.Builder builder, CreativeModeTabConfiguratorImpl configuratorImpl) {
        configuratorImpl.configure(builder);
        if (configuratorImpl.isHasSearchBar()) {
            builder.withSearchBar();
        }
        if (configuratorImpl.getIcons() != null) {
            builder.withTabFactory(other -> new CreativeModeTab(other) {
                @Nullable
                private ItemStack[] icons;

                @Override
                public ItemStack getIconItem() {
                    // stolen from XFactHD, thanks :)
                    if (this.icons == null) {
                        this.icons = configuratorImpl.getIcons().get();
                        Preconditions.checkState(this.icons.length > 0, "icons is empty");
                    }
                    int index = (int) (System.currentTimeMillis() / 2000) % this.icons.length;
                    return this.icons[index];
                }
            });
        }
    }
}
