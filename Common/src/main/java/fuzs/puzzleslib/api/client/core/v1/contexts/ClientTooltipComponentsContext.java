package fuzs.puzzleslib.api.client.core.v1.contexts;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

/**
 * register a client-side tooltip component factory
 */
@FunctionalInterface
public interface ClientTooltipComponentsContext {

    /**
     * register custom tooltip components
     *
     * @param type    common {@link TooltipComponent} class
     * @param factory factory for creating {@link ClientTooltipComponent} from <code>type</code>
     * @param <T>     type of common component
     */
    <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);
}
