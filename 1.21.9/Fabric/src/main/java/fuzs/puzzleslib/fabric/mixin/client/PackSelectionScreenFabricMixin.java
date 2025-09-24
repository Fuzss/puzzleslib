package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.fabric.impl.core.FabricProxy;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.gui.screens.packs.PackSelectionModel;
import net.minecraft.client.gui.screens.packs.PackSelectionScreen;
import net.minecraft.network.chat.Component;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.ModifyVariable;

import java.util.stream.Stream;

@Mixin(PackSelectionScreen.class)
abstract class PackSelectionScreenFabricMixin extends Screen {

    protected PackSelectionScreenFabricMixin(Component title) {
        super(title);
    }

    @ModifyVariable(method = "filterEntries", at = @At("HEAD"), argsOnly = true)
    private Stream<PackSelectionModel.Entry> filterEntries(Stream<PackSelectionModel.Entry> stream) {
        return stream.filter((PackSelectionModel.Entry entry) -> FabricProxy.get().notHidden(entry.getId()));
    }
}
