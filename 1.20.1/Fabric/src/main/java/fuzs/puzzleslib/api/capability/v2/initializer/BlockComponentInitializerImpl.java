package fuzs.puzzleslib.api.capability.v2.initializer;

import dev.onyxstudios.cca.api.v3.block.BlockComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.block.BlockComponentInitializer;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import fuzs.puzzleslib.impl.capability.v2.ComponentFactoryRegistry;
import fuzs.puzzleslib.impl.capability.v2.FabricCapabilityController;
import fuzs.puzzleslib.impl.capability.v2.data.ComponentHolder;
import net.minecraft.world.level.block.entity.BlockEntity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Function;

/**
 * A simple implementation of {@link BlockComponentInitializer} to allow for decentralized usage of Cardinal Components modules.
 * <p>This class is not supposed to be accessed by other mods in code, only from the Cardinal Components entry point
 * which needs to be added to every mod's <code>fabric.mod.json</code> that makes use of a block entity related capability.
 * The following must be added:
 * <pre><code>
 *   "entrypoints": {
 *     "cardinal-components": [
 *       "fuzs.puzzleslib.api.capability.v2.initializer.BlockComponentInitializerImpl"
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
public final class BlockComponentInitializerImpl implements BlockComponentInitializer {

    @Override
    public void registerBlockComponentFactories(BlockComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(BlockEntity.class, registry);
    }

    public static <T extends BlockEntity> ComponentFactoryRegistry<T> getBlockEntityFactory(Class<T> blockEntityType) {
        Objects.requireNonNull(blockEntityType, "block entity type is null");
        return (Object o, ComponentKey<ComponentHolder> componentKey, Function<T, ComponentHolder> factory) -> {
            ((BlockComponentFactoryRegistry) o).registerFor(blockEntityType, componentKey, factory::apply);
        };
    }
}
