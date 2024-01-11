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
import net.minecraft.server.packs.repository.PackSource;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.world.flag.FeatureFlagSet;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.Objects;
import java.util.Set;
import java.util.function.Supplier;

/**
 * A basic implementation of {@link PackResources} fit to be used for a built-in pack providing runtime generated assets.
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
     * <p>Set internally using {@link #buildPack(PackType, ResourceLocation, Supplier, Component, Component, boolean, boolean, boolean, FeatureFlagSet)}.
     */
    private ResourceLocation id;
    /**
     * The metadata for the <code>pack.mcmeta</code> section.
     * <p>Set internally using {@link #buildPack(PackType, ResourceLocation, Supplier, Component, Component, boolean, boolean, boolean, FeatureFlagSet)}.
     */
    private BuiltInMetadata metadata;
    /**
     * The pack type for this pack.
     * <p>Set internally using {@link #buildPack(PackType, ResourceLocation, Supplier, Component, Component, boolean, boolean, boolean, FeatureFlagSet)}.
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
                    }).orElse(null);
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
    protected final String getNamespace() {
        Objects.requireNonNull(this.id, "id is null");
        return this.id.getNamespace();
    }

    /**
     * An internal setup helper intended for setting up {@link DynamicPackResources} after the id has been set.
     */
    @ApiStatus.Internal
    void setup() {

    }

    @ApiStatus.Internal
    static Pack buildPack(PackType packType, ResourceLocation id, Supplier<AbstractModPackResources> factory, Component title, Component description, boolean required, boolean fixedPosition, boolean hidden, FeatureFlagSet features) {
        PackMetadataSection metadataSection = new PackMetadataSection(description, SharedConstants.getCurrentVersion().getPackVersion(packType));
        BuiltInMetadata metadata = BuiltInMetadata.of(PackMetadataSection.TYPE, metadataSection);
        Pack.Info info = CommonAbstractions.INSTANCE.createPackInfo(id, description, metadataSection.getPackFormat(), features, hidden);
        return Pack.create(id.toString(), title, required, $ -> {
            AbstractModPackResources packResources = factory.get();
            packResources.id = id;
            packResources.metadata = metadata;
            packResources.packType = packType;
            packResources.setup();
            return packResources;
        }, info, packType, Pack.Position.TOP, fixedPosition, PackSource.BUILT_IN);
    }
}
