package fuzs.puzzleslib.fabric.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import net.fabricmc.fabric.api.client.rendering.v1.TooltipComponentCallback;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.Objects;
import java.util.function.Function;

public final class ClientTooltipComponentsContextFabricImpl implements ClientTooltipComponentsContext {

    @SuppressWarnings("unchecked")
    @Override
    public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> tooltipComponentClazz, Function<? super T, ? extends ClientTooltipComponent> clientTooltipComponentFactory) {
        Objects.requireNonNull(tooltipComponentClazz, "tooltip component type is null");
        Objects.requireNonNull(clientTooltipComponentFactory, "tooltip component factory is null");
        TooltipComponentCallback.EVENT.register((TooltipComponent data) -> {
            if (data.getClass() == tooltipComponentClazz) return clientTooltipComponentFactory.apply((T) data);
            return null;
        });
    }
}
