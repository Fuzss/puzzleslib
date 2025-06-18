package fuzs.puzzleslib.api.client.core.v1.context;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

/**
 * Register a client-side tooltip component factory for creating a renderer from a {@link TooltipComponent}.
 */
@FunctionalInterface
public interface ClientTooltipComponentsContext {

    /**
     * Register a {@link ClientTooltipComponent}.
     *
     * @param tooltipComponentClazz         the common {@link TooltipComponent} class
     * @param clientTooltipComponentFactory the factory for creating {@link ClientTooltipComponent} from the common
     *                                      component
     * @param <T>                           the type of common component
     */
    <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> tooltipComponentClazz, Function<? super T, ? extends ClientTooltipComponent> clientTooltipComponentFactory);
}
