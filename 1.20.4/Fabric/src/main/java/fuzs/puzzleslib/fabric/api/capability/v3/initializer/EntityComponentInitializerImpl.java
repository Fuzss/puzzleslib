package fuzs.puzzleslib.fabric.api.capability.v3.initializer;

import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import fuzs.puzzleslib.api.capability.v3.data.CapabilityComponent;
import fuzs.puzzleslib.fabric.impl.capability.ComponentFactoryRegistrar;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.capability.data.ComponentAdapter;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Objects;
import java.util.function.Function;

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
@ApiStatus.Internal
public final class EntityComponentInitializerImpl implements EntityComponentInitializer {

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(Entity.class, registry);
    }

    public static <T extends Entity, C extends CapabilityComponent<T>> ComponentFactoryRegistrar<T, C> getEntityFactory(Class<T> entityType) {
        Objects.requireNonNull(entityType, "entity type is null");
        return (Object o, ComponentKey<ComponentAdapter<T, C>> componentKey, Function<T, ComponentAdapter<T, C>> factory) -> {
            ((EntityComponentFactoryRegistry) o).registerFor(entityType, componentKey, factory::apply);
        };
    }
}
