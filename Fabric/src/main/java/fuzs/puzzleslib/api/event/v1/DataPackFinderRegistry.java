package fuzs.puzzleslib.api.event.v1;

import fuzs.puzzleslib.impl.event.DataPackFinderRegistryImpl;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

/**
 * Allows for adding mod provided built-in data packs.
 * <p>The equivalent for resource packs is implemented via {@link fuzs.puzzleslib.api.client.event.v1.ResourcePackFinderRegistry}.
 * <p>In contrast to {@link net.fabricmc.fabric.api.resource.ResourceManagerHelper} this implementation also supports virtual / runtime generated packs,
 * not just physical packs located in the mod jar.
 */
public interface DataPackFinderRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    DataPackFinderRegistry INSTANCE = new DataPackFinderRegistryImpl();

    /**
     * Adds a new repository source that is added to the server pack sources every time they are created,
     * which happens on server starting (both internal and dedicated) and on the "Create World" screen.
     * <p>Packs that are enabled by default must return <code>true</code> for {@link Pack#isRequired()}.
     * Registering built-in packs that are not required, but should be enabled by default are not supported.
     *
     * @param repositorySource the repository source
     */
    void register(RepositorySource repositorySource);
}
