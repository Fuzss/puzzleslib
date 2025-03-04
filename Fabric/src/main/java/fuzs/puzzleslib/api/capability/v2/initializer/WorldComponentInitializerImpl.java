package fuzs.puzzleslib.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.world.WorldComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.world.WorldComponentInitializer;
import fuzs.puzzleslib.impl.capability.v2.ComponentFactoryRegistry;
import fuzs.puzzleslib.impl.capability.v2.FabricCapabilityController;
import fuzs.puzzleslib.impl.capability.v2.data.ComponentHolder;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A simple implementation of {@link WorldComponentInitializer} to allow for decentralized usage of Cardinal Components modules.
 * <p>This class is not supposed to be accessed by other mods in code, only from the Cardinal Components entry point
 * which needs to be added to every mod's <code>fabric.mod.json</code> that makes use of a level related capability.
 * The following must be added:
 * <pre><code>
 *   "entrypoints": {
 *     "cardinal-components": [
 *       "fuzs.puzzleslib.api.capability.v2.initializer.WorldComponentInitializerImpl"
 *     ]
 *   }
 * </code></pre>
 * Additionally, registering every individual component is achieved like so:
 * <pre><code>
 *   "custom": {
 *     "cardinal-components": [
 *       "${modId}:&lt;identifier&gt;"
 *     ]
 *   }
 * </code></pre>
 */
@ApiStatus.Internal
public final class WorldComponentInitializerImpl implements WorldComponentInitializer {

    @Override
    public void registerWorldComponentFactories(WorldComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(Level.class, registry);
    }

    public static ComponentFactoryRegistry<Level> getLevelFactory() {
        return (Object o, ComponentKey<ComponentHolder> componentKey, Function<Level, ComponentHolder> factory) -> {
            ((WorldComponentFactoryRegistry) o).register(componentKey, factory::apply);
        };
    }
}
