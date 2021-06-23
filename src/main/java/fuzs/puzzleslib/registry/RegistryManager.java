package fuzs.puzzleslib.registry;

import fuzs.puzzleslib.util.NamespaceUtil;
import com.google.common.collect.ArrayListMultimap;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.event.RegistryEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.registries.IForgeRegistry;
import net.minecraftforge.registries.IForgeRegistryEntry;

import javax.annotation.Nullable;

/**
 * handles registering to forge registries
 * heavily inspired by RegistryHelper found in Vazkii's AutoRegLib
 */
public class RegistryManager {

    /**
     * internal storage for collecting and registering registry entries
     */
    private final ArrayListMultimap<Class<?>, IForgeRegistryEntry<?>> registryEntries = ArrayListMultimap.create();

    /**
     * listener is added in main mod class so it's always puzzles lib itself and not the first mod registering something
     * @param evt all forge registry events
     */
    @SuppressWarnings("unused")
    @SubscribeEvent
    public void onRegistryRegister(RegistryEvent.Register<?> evt) {

        this.addAllToRegistry(evt.getRegistry());
    }

    /**
     * add all entries for registry type to registry
     * @param registry active registry
     * @param <T> type of registry entry
     */
    @SuppressWarnings("unchecked")
    private <T extends IForgeRegistryEntry<T>> void addAllToRegistry(IForgeRegistry<T> registry) {

        Class<T> type = registry.getRegistrySuperType();
        if (this.registryEntries.containsKey(type)) {

            for (IForgeRegistryEntry<?> entry : this.registryEntries.get(type)) {

                registry.register((T) entry);
            }

            this.registryEntries.removeAll(type);
        }
    }

    /**
     * register any type of registry entry with a path
     * @param entry entry to register
     */
    public void register(IForgeRegistryEntry<?> entry) {

        assert entry.getRegistryName() != null;
        this.register(null, entry);
    }

    /**
     * register any type of registry entry with a path
     * @param path path for new entry
     * @param entry entry to register
     */
    public void register(@Nullable String path, IForgeRegistryEntry<?> entry) {

        if (entry == null) {

            throw new IllegalArgumentException("Can't register null object.");
        }

        if (entry.getRegistryName() == null) {

            assert path != null;
            entry.setRegistryName(new ResourceLocation(NamespaceUtil.getActiveNamespace(), path));
        }

        this.registryEntries.put(entry.getRegistryType(), entry);
    }

}
