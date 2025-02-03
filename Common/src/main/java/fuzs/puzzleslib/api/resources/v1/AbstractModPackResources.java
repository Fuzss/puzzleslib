package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.resources.ModPackResourcesSupplier;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionType;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.repository.RepositorySource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A basic implementation of {@link PackResources} fit to be used for a built-in pack providing runtime generated
 * assets.
 * <p>This pack automatically uses the mod's mod logo for the pack icon.
 */
public class AbstractModPackResources implements PackResources {
    /**
     * Path to the mod logo inside the mod jar to be used in place of <code>pack.png</code> for the pack icon.
     * <p>Defaults to <code>mod_logo.png</code>, path is separated using "/".
     */
    protected final String modLogoPath;
    /**
     * The pack type for this pack, set internally during construction.
     */
    @Nullable
    private PackType packType;
    /**
     * Location info of this pack, set internally during construction.
     */
    @Nullable
    private PackLocationInfo info;
    /**
     * The metadata for the <code>pack.mcmeta</code> section, set internally during construction.
     */
    @Nullable
    private BuiltInMetadata metadata;

    /**
     * Simple constructor with default file path parameter for the pack icon.
     */
    public AbstractModPackResources() {
        this("mod_logo.png");
    }

    /**
     * Constructor with full control over the pack icon.
     */
    public AbstractModPackResources(String modLogoPath) {
        Objects.requireNonNull(modLogoPath, "mod logo path is null");
        this.modLogoPath = modLogoPath;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        String path = String.join("/", elements);
        if ("pack.png".equals(path)) {
            return ModLoaderEnvironment.INSTANCE.getModContainer(this.getNamespace())
                    .flatMap(container -> container.findResource(this.modLogoPath))
                    .<IoSupplier<InputStream>>map(modResource -> {
                        return () -> Files.newInputStream(modResource);
                    })
                    .orElse(null);
        }

        return null;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation location) {
        return null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {
        // NO-OP
    }

    @Override
    public Set<String> getNamespaces(PackType type) {
        Objects.requireNonNull(this.packType, "pack type is null");
        return this.packType == type ? Collections.singleton(this.getNamespace()) : Collections.emptySet();
    }

    @Override
    public @Nullable <T> T getMetadataSection(MetadataSectionType<T> type) throws IOException {
        Objects.requireNonNull(this.metadata, "metadata is null");
        return this.metadata.get(type);
    }

    @Override
    public PackLocationInfo location() {
        Objects.requireNonNull(this.info, "info is null");
        return this.info;
    }

    @Override
    public void close() {
        // NO-OP
    }

    /**
     * @return the namespace from the internal id
     */
    public String getNamespace() {
        return ResourceLocationHelper.parse(this.packId()).getNamespace();
    }

    /**
     * A helper method intended for setting up a pack with e.g. dynamically generated resources.
     * <p>
     * Runs after the id has been set.
     */
    @ApiStatus.OverrideOnly
    protected void setup() {
        // NO-OP
    }

    /**
     * Creates a new pack for registering a repository source.
     *
     * @param packType      type marking this pack as containing data or resource pack resources
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
     * @param features      {@link net.minecraft.world.flag.FeatureFlags} enabled through this pack
     * @return the pack to be used for creating a {@link RepositorySource}
     */
    public static Pack buildPack(PackType packType, ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, Pack.Position position, boolean fixedPosition, boolean hidden, FeatureFlagSet features) {
        PackLocationInfo info = new PackLocationInfo(id.toString(), title, PackSource.BUILT_IN, Optional.empty());
        Pack.ResourcesSupplier resourcesSupplier = ModPackResourcesSupplier.create(packType,
                info,
                createSupplier(factory),
                description);
        Pack.Metadata metadata = CommonAbstractions.INSTANCE.createPackInfo(id,
                description,
                PackCompatibility.COMPATIBLE,
                features,
                hidden);
        PackSelectionConfig config = new PackSelectionConfig(required, position, fixedPosition);
        return new Pack(info, resourcesSupplier, metadata, config);
    }

    private static ModPackResourcesSupplier.PackResourcesSupplier<AbstractModPackResources> createSupplier(Supplier<AbstractModPackResources> factory) {
        return (PackType packType, PackLocationInfo info, BuiltInMetadata metadata) -> {
            AbstractModPackResources packResources = factory.get();
            packResources.info = info;
            packResources.metadata = metadata;
            packResources.packType = packType;
            packResources.setup();
            return packResources;
        };
    }
}
