package fuzs.puzzleslib.fabric.api.capability.v3.initializer;

import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import org.jetbrains.annotations.ApiStatus;

/**
 * A simple implementation of {@link EntityComponentInitializer} to allow for decentralized usage of Cardinal Components modules.
 * <p>This class is not supposed to be accessed by other mods in code, only from the Cardinal Components entry point
 * which needs to be added to every mod's <code>fabric.mod.json</code> that makes use of an entity related capability.
 * The following must be added:
 * <pre><code>
 *   "entrypoints": {
 *     "cardinal-components": [
 *       "fuzs.puzzleslib.fabric.api.capability.v3.initializer.EntityComponentInitializerImpl"
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
@Deprecated(forRemoval = true)
@ApiStatus.Internal
public final class EntityComponentInitializerImpl implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        // NO-OP
    }
}
