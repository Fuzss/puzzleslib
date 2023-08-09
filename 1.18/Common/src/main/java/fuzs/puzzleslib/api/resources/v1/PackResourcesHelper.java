package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.network.chat.Component;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;

import java.util.function.Consumer;
import java.util.function.Supplier;

/**
 * This class provides some simple helper methods for constructing simple {@link net.minecraft.server.packs.PackResources} implementation for either the client or server.
 */
public final class PackResourcesHelper {

    private PackResourcesHelper() {

    }

    /**
     * Creates a new resource pack repository source (for the client).
     * <p>Can be added via {@link fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to the left side; this is used for the vanilla resource pack
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(Supplier<AbstractModPackResources> factory, String id, Component title, Component description, boolean required, boolean fixedPosition) {
        return (Consumer<Pack> consumer, Pack.PackConstructor packConstructor) -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.CLIENT_RESOURCES, factory, id, title, description, required, fixedPosition));
        };
    }

    /**
     * Creates a new data pack repository source (for the server).
     * <p>Can be added via {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to the left side; this is used for the vanilla resource pack
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(Supplier<AbstractModPackResources> factory, String id, Component title, Component description, boolean required, boolean fixedPosition) {
        return (Consumer<Pack> consumer, Pack.PackConstructor packConstructor) -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.SERVER_DATA, factory, id, title, description, required, fixedPosition));
        };
    }
}
