package fuzs.puzzleslib.api.core.v1.context;

import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.Objects;

/**
 * Register custom and built-in data &amp; resource packs.
 */
public interface PackRepositorySourcesContext {

    /**
     * Register an additional {@link RepositorySource} when a new
     * {@link net.minecraft.server.packs.repository.PackRepository} is created.
     * <p>
     * Context can be used for registering both client (for resource packs) and server (for data packs) repository
     * sources.
     *
     * @param repositorySource the repository source to add
     */
    void registerRepositorySource(RepositorySource repositorySource);

    /**
     * Register a built-in pack bundled with the mod.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param resourceLocation the name of the pack in the {@code resources} directory
     */
    @Deprecated(forRemoval = true)
    default void registerBuiltInPack(ResourceLocation resourceLocation) {
        Objects.requireNonNull(resourceLocation, "resource location is null");
        this.registerBuiltInPack(resourceLocation, Component.literal(resourceLocation.toString()), false);
    }

    /**
     * Register a built-in pack bundled with the mod.
     * <ul>
     *     <li>Data pack path: {@code data/<modId>/datapacks/<path>}</li>
     *     <li>Resource pack path: {@code assets/<modId>/resourcepacks/<path>}</li>
     * </ul>
     *
     * @param resourceLocation the name of the pack in the {@code resources} directory
     * @param displayName      the name component for the created pack
     * @param shouldAddAutomatically       is this pack always enabled and cannot be turned off
     */
    void registerBuiltInPack(ResourceLocation resourceLocation, Component displayName, boolean shouldAddAutomatically);
}
