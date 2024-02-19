package fuzs.puzzleslib.impl.init;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.init.v3.MinecartTypeRegistry;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;
import org.jetbrains.annotations.Nullable;

import java.util.Collections;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;

public final class MinecartTypeRegistryImpl implements MinecartTypeRegistry {
    private static final Map<AbstractMinecart.Type, Factory> MINECART_FACTORIES = Collections.synchronizedMap(Maps.newIdentityHashMap());

    @Override
    public void register(AbstractMinecart.Type type, Factory factory) {
        Objects.requireNonNull(type, "type is null");
        Objects.requireNonNull(factory, "factory is null");
        MINECART_FACTORIES.put(type, factory);
    }

    public static Optional<AbstractMinecart> createMinecartForType(@Nullable AbstractMinecart.Type type, Level level, double x, double y, double z) {
        // be careful with type, custom minecarts from other mods might do things differently from us without creating a custom type
        if (type != null && MINECART_FACTORIES.containsKey(type)) {
            return Optional.of(MINECART_FACTORIES.get(type)).map(factory -> factory.create(level, x, y, z));
        } else {
            return Optional.empty();
        }
    }
}
