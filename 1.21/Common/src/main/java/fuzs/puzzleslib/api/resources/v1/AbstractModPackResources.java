package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.*;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

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
public abstract class AbstractModPackResources implements PackResources {
    /**
     * Path to the mod logo inside the mod jar to be used in place of <code>pack.png</code> for the pack icon.
     * <p>Defaults to <code>mod_logo.png</code>, path is separated using "/".
     */
    protected final String modLogoPath;
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
     * The pack type for this pack, set internally during construction.
     */
    @Nullable
    private PackType packType;

    /**
     * Simple constructor with default file path parameter for the pack icon.
     */
    protected AbstractModPackResources() {
        this("mod_logo.png");
    }

    /**
     * Constructor with full control over the pack icon.
     */
    protected AbstractModPackResources(String modLogoPath) {
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

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        Objects.requireNonNull(this.metadata, "metadata is null");
        return this.metadata.get(deserializer);
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
    protected String getNamespace() {
        return ResourceLocationHelper.parse(this.packId()).getNamespace();
    }

    /**
     * A helper method intended for setting up a pack with e.g. dynamically generated resources.
     * <p>
     * Runs after the id has been set.
     */
    protected void setup() {
        // NO-OP
    }

    @ApiStatus.Internal
    static Pack buildPack(PackType packType, ResourceLocation identifier, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, Pack.Position position, boolean fixedPosition, boolean hidden, FeatureFlagSet features) {
        PackLocationInfo info = new PackLocationInfo(identifier.toString(),
                title,
                PackSource.BUILT_IN,
                Optional.empty()
        );
        Pack.ResourcesSupplier resourcesSupplier = ModPackResourcesSupplier.create(packType,
                info,
                factory,
                description
        );
        Pack.Metadata metadata = CommonAbstractions.INSTANCE.createPackInfo(identifier,
                description,
                PackCompatibility.COMPATIBLE,
                features,
                hidden
        );
        PackSelectionConfig config = new PackSelectionConfig(required, position, fixedPosition);
        return new Pack(info, resourcesSupplier, metadata, config);
    }

    private record ModPackResourcesSupplier(PackType packType, PackLocationInfo info, Supplier<AbstractModPackResources> factory, BuiltInMetadata metadata) implements Pack.ResourcesSupplier {

        static ModPackResourcesSupplier create(PackType packType, PackLocationInfo info, Supplier<AbstractModPackResources> factory, Component description) {
            PackMetadataSection metadataSection = new PackMetadataSection(description,
                    SharedConstants.getCurrentVersion().getPackVersion(packType),
                    Optional.empty()
            );
            return new ModPackResourcesSupplier(packType, info, factory,
                    BuiltInMetadata.of(PackMetadataSection.TYPE, metadataSection)
            );
        }

        @Override
        public PackResources openPrimary(PackLocationInfo info) {
            return this.getAndSetupPackResources();
        }

        @Override
        public PackResources openFull(PackLocationInfo info, Pack.Metadata packMetadata) {
            return this.getAndSetupPackResources();
        }

        private AbstractModPackResources getAndSetupPackResources() {
            AbstractModPackResources packResources = this.factory.get();
            packResources.info = this.info;
            packResources.metadata = this.metadata;
            packResources.packType = this.packType;
            packResources.setup();
            return packResources;
        }
    }
}
