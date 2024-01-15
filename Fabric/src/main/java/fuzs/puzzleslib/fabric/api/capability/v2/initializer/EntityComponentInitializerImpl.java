package fuzs.puzzleslib.fabric.api.capability.v2.initializer;

import com.google.common.collect.Maps;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import fuzs.puzzleslib.api.capability.v3.data.CopyStrategy;
import fuzs.puzzleslib.fabric.impl.capability.ComponentFactoryRegistry;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.capability.data.ComponentHolder;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import org.jetbrains.annotations.ApiStatus;

import java.util.Map;
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
 *       "fuzs.puzzleslib.api.capability.v2.initializer.EntityComponentInitializerImpl"
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
    private static final Map<CopyStrategy, RespawnCopyStrategy<Component>> COPY_STRATEGY_CONVERSIONS = Maps.immutableEnumMap(Map.of(CopyStrategy.ALWAYS, RespawnCopyStrategy.ALWAYS_COPY, CopyStrategy.KEEP_PLAYER_INVENTORY, RespawnCopyStrategy.INVENTORY, CopyStrategy.NEVER, RespawnCopyStrategy.LOSSLESS_ONLY));

    @Override
    public void registerEntityComponentFactories(EntityComponentFactoryRegistry registry) {
        FabricCapabilityController.registerComponentFactories(Entity.class, registry);
    }

    public static <T extends Entity> ComponentFactoryRegistry<T> getEntityFactory(Class<T> entityType) {
        Objects.requireNonNull(entityType, "entity type is null");
        return (Object o, ComponentKey<ComponentHolder> componentKey, Function<T, ComponentHolder> factory) -> {
            ((EntityComponentFactoryRegistry) o).registerFor(entityType, componentKey, factory::apply);
        };
    }

    public static ComponentFactoryRegistry<Player> getPlayerFactory(CopyStrategy copyStrategy) {
        Objects.requireNonNull(copyStrategy, "player respawn copy strategy is null");
        return (Object o, ComponentKey<ComponentHolder> componentKey, Function<Player, ComponentHolder> factory) -> {
            RespawnCopyStrategy<Component> respawnCopyStrategy = COPY_STRATEGY_CONVERSIONS.get(copyStrategy);
            Objects.requireNonNull(respawnCopyStrategy, "respawn copy strategy is null");
            ((EntityComponentFactoryRegistry) o).registerForPlayers(componentKey, factory::apply, respawnCopyStrategy);
        };
    }
}
