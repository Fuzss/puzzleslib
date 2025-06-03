package fuzs.puzzleslib.neoforge.mixin.client.accessor;

import net.neoforged.neoforge.client.event.RegisterGuiLayersEvent;
import net.neoforged.neoforge.client.gui.GuiLayerManager;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

import java.util.List;

@Mixin(RegisterGuiLayersEvent.class)
public interface RegisterGuiLayersEventAccessor {

    @Accessor("layers")
    List<GuiLayerManager.NamedLayer> puzzleslib$getLayers();
}
