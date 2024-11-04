package fuzs.puzzleslib.api.init.v3;

import fuzs.puzzleslib.impl.init.MinecartTypeRegistryImpl;
import net.minecraft.world.entity.vehicle.AbstractMinecart;
import net.minecraft.world.level.Level;

/**
 * Register a {@link AbstractMinecart} factory for a custom {@link AbstractMinecart.Type} that is created when an
 * instance of {@link net.minecraft.world.item.MinecartItem} is placed in the world by a player or dispenser.
 * <p>
 * The type must be created separately, e.g. using <a href="https://github.com/Fuzss/extensibleenums">Extensible Enums</a>.
 */
public interface MinecartTypeRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    MinecartTypeRegistry INSTANCE = new MinecartTypeRegistryImpl();

    /**
     * Register the custom minecart type.
     *
     * @param type    minecart type
     * @param factory minecart entity factory
     */
    void register(AbstractMinecart.Type type, Factory factory);

    /**
     * Factory for creating a custom minecart entity.
     */
    interface Factory {

        /**
         * @param level level to spawn the entity in
         * @param x     x-position to place the entity at
         * @param y     y-position to place the entity at
         * @param z     z-position to place the entity at
         * @return minecart entity
         */
        AbstractMinecart create(Level level, double x, double y, double z);
    }
}
