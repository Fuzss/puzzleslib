package fuzs.puzzleslib.client.core;

import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Forge client factories
 */
public final class ForgeClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId) {
        return constructor -> ForgeClientModConstructor.construct(modId, constructor);
    }

    @Override
    public CreativeModeTab creativeTab(String modId, String tabId, Supplier<ItemStack> stackSupplier) {
        return new CreativeModeTab(modId + "." + tabId) {

            @Override
            public ItemStack makeIcon() {
                return stackSupplier.get();
            }
        };
    }
}
