package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

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
    @Deprecated(forRemoval = true)
    public static RepositorySource buildClientPack(Supplier<AbstractModPackResources> factory, String id, Component title, Component description, boolean required, boolean fixedPosition) {
        return buildClientPack(new ResourceLocation(id, id), factory, title, description, required, fixedPosition);
    }

    /**
     * Creates a new resource pack repository source (for the client).
     * <p>Can be added via {@link fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to the left side; this is used for the vanilla resource pack
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, boolean fixedPosition) {
        return buildClientPack(id, factory, title, description, required, fixedPosition, false, FeatureFlagSet.of());
    }

    /**
     * Creates a new hidden resource pack repository source (for the client).
     * <p>Can be added via {@link fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param id      id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory) {
        return buildClientPack(id, factory, CommonComponents.EMPTY, CommonComponents.EMPTY, true, true, true, FeatureFlagSet.of());
    }

    /**
     * Creates a new resource pack repository source (for the client).
     * <p>Can be added via {@link fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to the left side; this is used for the vanilla resource pack
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @param hidden        controls whether the pack is hidden from user-facing screens like the resource pack and data pack selection screens, only available on Forge
     * @param features      {@link net.minecraft.world.flag.FeatureFlag}s provided by this pack
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, boolean fixedPosition, boolean hidden, FeatureFlagSet features) {
        return consumer -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.CLIENT_RESOURCES, id, factory, title, description, required, fixedPosition, hidden, features));
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
    @Deprecated(forRemoval = true)
    public static RepositorySource buildServerPack(Supplier<AbstractModPackResources> factory, String id, Component title, Component description, boolean required, boolean fixedPosition) {
        return buildServerPack(new ResourceLocation(id, id), factory, title, description, required, fixedPosition);
    }

    /**
     * Creates a new data pack repository source (for the server).
     * <p>Can be added via {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to the left side; this is used for the vanilla resource pack
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, boolean fixedPosition) {
        return buildServerPack(id, factory, title, description, required, fixedPosition, false, FeatureFlagSet.of());
    }

    /**
     * Creates a new hidden data pack repository source (for the server).
     * <p>Can be added via {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param id      id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory) {
        return buildServerPack(id, factory, CommonComponents.EMPTY, CommonComponents.EMPTY, true, true, true, FeatureFlagSet.of());
    }

    /**
     * Creates a new data pack repository source (for the server).
     * <p>Can be added via {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be moved to the left side; this is used for the vanilla resource pack
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @param hidden        controls whether the pack is hidden from user-facing screens like the resource pack and data pack selection screens, only available on Forge
     * @param features      {@link net.minecraft.world.flag.FeatureFlag}s provided by this pack
     * @return the {@link RepositorySource} to be added to the {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, boolean fixedPosition, boolean hidden, FeatureFlagSet features) {
        return consumer -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.SERVER_DATA, id, factory, title, description, required, fixedPosition, hidden, features));
        };
    }
}
