package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.world.flag.FeatureFlagSet;

import java.util.function.Supplier;

/**
 * This class provides some simple helper methods for constructing simple
 * {@link net.minecraft.server.packs.PackResources} implementation for either the client or server.
 */
public final class PackResourcesHelper {

    private PackResourcesHelper() {

    }

    /**
     * Creates a new resource pack repository source (for the client).
     * <p>Can be added via
     * {@link
     * fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param id      id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param hidden  controls whether the pack is hidden from user-facing screens like the resource pack and data pack
     *                selection screens
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean hidden) {
        return buildClientPack(id, factory, true, Pack.Position.TOP, hidden, hidden);
    }

    /**
     * Creates a new resource pack repository source (for the client).
     * <p>Can be added via
     * {@link
     * fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be
     *                      moved to the left side; this is used for the vanilla resource pack
     * @param position      insertion end in the pack list, new packs are usually inserted at the top above vanilla
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @param hidden        controls whether the pack is hidden from user-facing screens like the resource pack and data
     *                      pack selection screens
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean required, Pack.Position position, boolean fixedPosition, boolean hidden) {
        return consumer -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.CLIENT_RESOURCES,
                    id,
                    factory,
                    getPackTitle(PackType.CLIENT_RESOURCES),
                    getPackDescription(id.getNamespace()),
                    required,
                    position,
                    fixedPosition,
                    hidden,
                    FeatureFlagSet.of()
            ));
        };
    }

    /**
     * Creates a new resource pack repository source (for the client).
     * <p>Can be added via
     * {@link
     * fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be
     *                      moved to the left side; this is used for the vanilla resource pack
     * @param position      insertion end in the pack list, new packs are usually inserted at the top above vanilla
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @param hidden        controls whether the pack is hidden from user-facing screens like the resource pack and data
     *                      pack selection screens
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildClientPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, Pack.Position position, boolean fixedPosition, boolean hidden) {
        return consumer -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.CLIENT_RESOURCES,
                    id,
                    factory,
                    title,
                    description,
                    required,
                    position,
                    fixedPosition,
                    hidden,
                    FeatureFlagSet.of()
            ));
        };
    }

    /**
     * Create a simple pack title for a {@link PackType}.
     *
     * @param packType pack type for title
     * @return title component
     */
    public static Component getPackTitle(PackType packType) {
        return Component.literal(
                "Generated " + (packType == PackType.CLIENT_RESOURCES ? "Resource" : "Data") + " Pack");
    }

    /**
     * Create a fancy pack description for dynamic resources from a mod.
     *
     * @param modId the source mod for the dynamic pack
     * @return description component
     */
    public static Component getPackDescription(String modId) {
        return ModLoaderEnvironment.INSTANCE.getModContainer(modId).map(ModContainer::getDisplayName).map(name -> {
            return Component.literal(name + " Dynamic Resources");
        }).orElseGet(() -> Component.literal("Dynamic Resources (" + modId + ")"));
    }

    /**
     * Creates a new hidden data pack repository source (for the server).
     * <p>Can be added via
     * {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param id      id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param hidden  controls whether the pack is hidden from user-facing screens like the resource pack and data pack
     *                selection screens
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean hidden) {
        return buildServerPack(id, factory, true, Pack.Position.TOP, hidden, hidden);
    }

    /**
     * Creates a new data pack repository source (for the server).
     * <p>Can be added via
     * {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be
     *                      moved to the left side; this is used for the vanilla resource pack
     * @param position      insertion end in the pack list, new packs are usually inserted at the top above vanilla
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @param hidden        controls whether the pack is hidden from user-facing screens like the resource pack and data
     *                      pack selection screens, only available on Forge
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, boolean required, Pack.Position position, boolean fixedPosition, boolean hidden) {
        return consumer -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.SERVER_DATA,
                    id,
                    factory,
                    getPackTitle(PackType.SERVER_DATA),
                    getPackDescription(id.getNamespace()),
                    required,
                    position,
                    fixedPosition,
                    hidden,
                    FeatureFlagSet.of()
            ));
        };
    }

    /**
     * Creates a new data pack repository source (for the server).
     * <p>Can be added via
     * {@link fuzs.puzzleslib.api.core.v1.ModConstructor#onAddDataPackFinders(PackRepositorySourcesContext)}.
     *
     * @param id            id for the pack, used for internal references and is stored in <code>options.txt</code>
     * @param factory       {@link net.minecraft.server.packs.PackResources} implementation supplier
     * @param title         the title of this pack shown in the pack selection screen
     * @param description   the description for this pack shown in the pack selection screen
     * @param required      a required pack cannot be disabled, like in the pack selection screen the pack cannot be
     *                      moved to the left side; this is used for the vanilla resource pack
     * @param position      insertion end in the pack list, new packs are usually inserted at the top above vanilla
     * @param fixedPosition a fixed pack cannot be moved up or down, like a server or world resource pack
     * @param hidden        controls whether the pack is hidden from user-facing screens like the resource pack and data
     *                      pack selection screens, only available on Forge
     * @return the {@link RepositorySource} to be added to the
     *         {@link net.minecraft.server.packs.repository.PackRepository}
     */
    public static RepositorySource buildServerPack(ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, Pack.Position position, boolean fixedPosition, boolean hidden) {
        return consumer -> {
            consumer.accept(AbstractModPackResources.buildPack(PackType.SERVER_DATA,
                    id,
                    factory,
                    title,
                    description,
                    required,
                    position,
                    fixedPosition,
                    hidden,
                    FeatureFlagSet.of()
            ));
        };
    }
}
