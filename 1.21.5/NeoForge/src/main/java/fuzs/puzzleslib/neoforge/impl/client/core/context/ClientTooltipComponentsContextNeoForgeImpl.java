package fuzs.puzzleslib.neoforge.impl.client.core.context;

import fuzs.puzzleslib.api.client.core.v1.context.ClientTooltipComponentsContext;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;
import net.neoforged.neoforge.client.event.RegisterClientTooltipComponentFactoriesEvent;

import java.util.Objects;
import java.util.function.Function;

public record ClientTooltipComponentsContextNeoForgeImpl(RegisterClientTooltipComponentFactoriesEvent evt) implements ClientTooltipComponentsContext {

    @Override
    public <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory) {
        Objects.requireNonNull(type, "tooltip component type is null");
        Objects.requireNonNull(factory, "tooltip component factory is null");
        this.evt.register(type, factory);
    }
}
