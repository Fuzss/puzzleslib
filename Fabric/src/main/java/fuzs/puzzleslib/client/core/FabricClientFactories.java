package fuzs.puzzleslib.client.core;

import net.fabricmc.fabric.api.client.itemgroup.FabricItemGroupBuilder;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * Fabric client factories
 */
public final class FabricClientFactories implements ClientFactories {

    @Override
    public Consumer<ClientModConstructor> clientModConstructor(String modId) {
        return constructor -> FabricClientModConstructor.construct(modId, constructor);
    }

    @Override
    public CreativeModeTab creativeTab(String modId, String tabId, Supplier<ItemStack> stackSupplier) {
        return FabricItemGroupBuilder.build(new ResourceLocation(modId, tabId), stackSupplier);
    }
}
