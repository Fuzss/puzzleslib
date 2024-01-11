package fuzs.puzzleslib.fabric.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.chunk.ChunkComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.fabric.impl.capability.ComponentFactoryRegistry;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.capability.data.ComponentHolder;
import net.minecraft.world.level.chunk.ChunkAccess;
import net.minecraft.world.level.chunk.LevelChunk;
import org.jetbrains.annotations.ApiStatus;

import java.util.function.Function;

/**
 * A simple implementation of {@link ChunkComponentInitializer} to allow for decentralized usage of Cardinal Components modules.
 * <p>This class is not supposed to be accessed by other mods in code, only from the Cardinal Components entry point
 * which needs to be added to every mod's <code>fabric.mod.json</code> that makes use of a chunk related capability.
 * The following must be added:
 * <pre><code>
 *   "entrypoints": {
 *     "cardinal-components": [
 *       "fuzs.puzzleslib.api.capability.v2.initializer.ChunkComponentInitializerImpl"
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
public final class ChunkComponentInitializerImpl implements ChunkComponentInitializer {

    @Override
    public void registerChunkComponentFactories(ChunkComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(LevelChunk.class, registry);
    }

    public static ComponentFactoryRegistry<ChunkAccess> getLevelChunkFactory() {
        return (Object o, ComponentKey<ComponentHolder> componentKey, Function<ChunkAccess, ComponentHolder> factory) -> {
            ((ChunkComponentFactoryRegistry) o).register(componentKey, factory::apply);
        };
    }
}
