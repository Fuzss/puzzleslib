package fuzs.puzzleslib.fabric.mixin.client;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.fabric.impl.core.FabricAbstractions;
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

    @ModifyVariable(method = "updateList", at = @At("LOAD"), argsOnly = true)
    private Stream<PackSelectionModel.Entry> updateList(Stream<PackSelectionModel.Entry> models) {
        return models.filter(entry -> ((FabricAbstractions) CommonAbstractions.INSTANCE).notHidden(entry.getId()));
    }
}
