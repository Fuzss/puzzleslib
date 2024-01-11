package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.impl.client.event.ResourcePackFinderRegistryImpl;
import net.minecraft.client.Minecraft;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

/**
 * Allows for adding mod provided built-in resource packs.
 * <p>The equivalent for data packs is implemented via {@link fuzs.puzzleslib.api.event.v1.DataPackFinderRegistry}.
 * <p>In contrast to {@link net.fabricmc.fabric.api.resource.ResourceManagerHelper} this implementation also supports virtual / runtime generated packs,
 * not just physical packs located in the mod jar.
 */
public interface ResourcePackFinderRegistry {
    /**
     * The singleton instance of the decorator registry.
     * Use this instance to call the methods in this interface.
     */
    ResourcePackFinderRegistry INSTANCE = new ResourcePackFinderRegistryImpl();

    /**
     * Adds a new repository source to the client resource pack repository.
     * <p>The source is added instantly to {@link Minecraft#getResourcePackRepository()},
     * since the corresponding {@link net.minecraft.server.packs.repository.PackRepository} has already been created.
     * <p>Packs that are enabled by default must return <code>true</code> for {@link Pack#isRequired()}.
     * Registering built-in packs that are not required, but should be enabled by default are not supported.
     *
     * @param repositorySource the repository source
     */
    void register(RepositorySource repositorySource);
}
