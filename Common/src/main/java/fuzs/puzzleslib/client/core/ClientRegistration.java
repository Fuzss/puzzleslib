package fuzs.puzzleslib.client.core;

import fuzs.puzzleslib.client.init.builder.ModSpriteParticleRegistration;
import net.minecraft.client.gui.screens.inventory.tooltip.ClientTooltipComponent;
import net.minecraft.client.particle.ParticleProvider;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleType;
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

    /**
     * registers a factory for a particle type client side
     * @param type     particle type (registered separately)
     * @param provider particle factory
     * @param <T>      type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ParticleProvider<T> provider);

    /**
     * registers a factory for a particle type client side
     * @param type     particle type (registered separately)
     * @param factory particle factory
     * @param <T>      type of particle
     */
    <T extends ParticleOptions> void registerParticleProvider(ParticleType<T> type, ModSpriteParticleRegistration<T> factory);
}
