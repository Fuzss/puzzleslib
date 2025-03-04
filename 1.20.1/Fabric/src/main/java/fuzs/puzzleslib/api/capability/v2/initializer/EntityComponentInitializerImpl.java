package fuzs.puzzleslib.api.capability.v2.initializer;

import com.google.common.collect.Maps;
import dev.onyxstudios.cca.api.v3.component.Component;
import dev.onyxstudios.cca.api.v3.component.ComponentKey;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentFactoryRegistry;
import dev.onyxstudios.cca.api.v3.entity.EntityComponentInitializer;
import dev.onyxstudios.cca.api.v3.entity.RespawnCopyStrategy;
import fuzs.puzzleslib.api.capability.v2.data.PlayerRespawnCopyStrategy;
import fuzs.puzzleslib.impl.capability.v2.ComponentFactoryRegistry;
import fuzs.puzzleslib.impl.capability.v2.FabricCapabilityController;
import fuzs.puzzleslib.impl.capability.v2.data.ComponentHolder;
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
    private static final Map<PlayerRespawnCopyStrategy, RespawnCopyStrategy<Component>> COPY_STRATEGY_CONVERSIONS = Maps.immutableEnumMap(Map.of(PlayerRespawnCopyStrategy.ALWAYS, RespawnCopyStrategy.ALWAYS_COPY, PlayerRespawnCopyStrategy.KEEP_INVENTORY, RespawnCopyStrategy.INVENTORY, PlayerRespawnCopyStrategy.RETURNING_FROM_END, RespawnCopyStrategy.LOSSLESS_ONLY, PlayerRespawnCopyStrategy.NEVER, RespawnCopyStrategy.NEVER_COPY));

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

    public static ComponentFactoryRegistry<Player> getPlayerFactory(PlayerRespawnCopyStrategy playerRespawnCopyStrategy) {
        Objects.requireNonNull(playerRespawnCopyStrategy, "player respawn copy strategy is null");
        return (Object o, ComponentKey<ComponentHolder> componentKey, Function<Player, ComponentHolder> factory) -> {
            RespawnCopyStrategy<Component> respawnCopyStrategy = COPY_STRATEGY_CONVERSIONS.get(playerRespawnCopyStrategy);
            Objects.requireNonNull(respawnCopyStrategy, "respawn copy strategy is null");
            ((EntityComponentFactoryRegistry) o).registerForPlayers(componentKey, factory::apply, respawnCopyStrategy);
        };
    }
}
