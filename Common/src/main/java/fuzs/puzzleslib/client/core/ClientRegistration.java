package fuzs.puzzleslib.client.core;

import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.world.inventory.tooltip.TooltipComponent;

import java.util.function.Function;

/**
 * a collection of utility methods for registering client side content
 */
public interface ClientRegistration {

    /**
     * register custom tooltip components
     * @param type common {@link TooltipComponent} class
     * @param factory factory for creating {@link ClientTooltipComponent} from <code>type</code>
     * @param <T>     type of common component
     */
    <T extends TooltipComponent> void registerClientTooltipComponent(Class<T> type, Function<? super T, ? extends ClientTooltipComponent> factory);
}
