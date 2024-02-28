package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.BuiltInMetadata;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
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
     * Id of this pack.
     * <p>Set internally using
     * {@link #buildPack(PackType, ResourceLocation, Supplier, Component, Component, boolean, Pack.Position, boolean,
     * boolean, FeatureFlagSet)}.
     */
    private ResourceLocation id;
    /**
     * The metadata for the <code>pack.mcmeta</code> section.
     * <p>Set internally using
     * {@link #buildPack(PackType, ResourceLocation, Supplier, Component, Component, boolean, Pack.Position, boolean,
     * boolean, FeatureFlagSet)}.
     */
    private BuiltInMetadata metadata;
    /**
     * The pack type for this pack.
     * <p>Set internally using
     * {@link #buildPack(PackType, ResourceLocation, Supplier, Component, Component, boolean, Pack.Position, boolean,
     * boolean, FeatureFlagSet)}.
     */
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
    public String packId() {
        return this.id.toString();
    }

    @Override
    public boolean isBuiltin() {
        return true;
    }

    @Override
    public void close() {

    }

    /**
     * @return the namespace from the internal id
     */
    public final String getNamespace() {
        Objects.requireNonNull(this.id, "id is null");
        return this.id.getNamespace();
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
        PackMetadataSection metadataSection = new PackMetadataSection(description,
                SharedConstants.getCurrentVersion().getPackVersion(packType),
                Optional.empty()
        );
        BuiltInMetadata metadata = BuiltInMetadata.of(PackMetadataSection.TYPE, metadataSection);
        Pack.Info info = CommonAbstractions.INSTANCE.createPackInfo(identifier,
                description,
                PackCompatibility.COMPATIBLE,
                features,
                hidden
        );
        return Pack.create(identifier.toString(), title, required, new Pack.ResourcesSupplier() {
            @Override
            public PackResources openPrimary(String id) {
                AbstractModPackResources packResources = factory.get();
                packResources.id = identifier;
                packResources.metadata = metadata;
                packResources.packType = packType;
                packResources.setup();
                return packResources;
            }

            @Override
            public PackResources openFull(String id, Pack.Info info) {
                return this.openPrimary(id);
            }
        }, info, position, fixedPosition, PackSource.BUILT_IN);
    }
}
