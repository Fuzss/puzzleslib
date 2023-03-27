package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
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
import java.util.function.Supplier;

/**
 * A basic implementation of {@link PackResources} fit to be used for a built-in pack providing runtime generated assets.
 * <p>This pack automatically uses the mod's mod logo for the pack icon.
 */
public abstract class AbstractModPackResources implements PackResources {
    /**
     * The id of the mod from whose jar to retrieve the logo from.
     * <p>Defaults to the pack id from {@link #id}.
     */
    private String logoModId;
    /**
     * Path to the mod logo inside the mod jar to be used in place of <code>pack.png</code> for the pack icon.
     * <p>Defaults to <code>mod_logo.png</code>, path is separated using "/".
     */
    private final String modLogoPath;
    /**
     * Id of this pack, set internally using {@link #buildPack(PackType, Supplier, String, Component, Component, boolean, boolean)}.
     */
    private String id;
    /**
     * The metadata for the <code>pack.mcmeta</code> section, set internally using {@link #buildPack(PackType, Supplier, String, Component, Component, boolean, boolean)}.
     */
    private BuiltInMetadata metadata;

    /**
     * Simple constructor with default parameters regarding the pack icon.
     */
    protected AbstractModPackResources() {
        this(null);
    }

    /**
     * Constructor that allows setting a custom mod id to retrieve <code>mod_logo.png</code> from.
     * <p>A null value of mod id will default to the pack id from {@link #id}.
     */
    protected AbstractModPackResources(@Nullable String logoModId) {
        this(logoModId, "mod_logo.png");
    }

    /**
     * Constructor with full control over the pack icon.
     * <p>A null value of mod id will default to the pack id from {@link #id}.
     */
    protected AbstractModPackResources(@Nullable String logoModId, String modLogoPath) {
        this.logoModId = logoModId;
        this.modLogoPath = modLogoPath;
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getRootResource(String... elements) {
        String path = String.join("/", elements);
        if ("pack.png".equals(path)) {
            return ModLoaderEnvironment.INSTANCE.findModResource(this.logoModId, this.modLogoPath).<IoSupplier<InputStream>>map(modResource -> {
                return () -> Files.newInputStream(modResource);
            }).orElse(null);
        }
        return null;
    }

    @Override
    public void listResources(PackType packType, String namespace, String path, ResourceOutput resourceOutput) {

    }

    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        return this.metadata.get(deserializer);
    }

    @Override
    public String packId() {
        return this.id;
    }

    @Override
    public void close() {

    }

    @ApiStatus.Internal
    static Pack buildPack(PackType packType, Supplier<AbstractModPackResources> factory, String id, Component title, Component description, boolean required, boolean fixedPosition) {
        PackMetadataSection metadataSection = new PackMetadataSection(description, packType.getVersion(SharedConstants.getCurrentVersion()));
        BuiltInMetadata metadata = BuiltInMetadata.of(PackMetadataSection.TYPE, metadataSection);
        Pack.Info info = new Pack.Info(description, metadataSection.getPackFormat(), FeatureFlagSet.of());
        return Pack.create(id, title, required, $ -> {
            AbstractModPackResources packResources = factory.get();
            packResources.id = id;
            packResources.metadata = metadata;
            if (packResources.logoModId == null) {
                packResources.logoModId = id;
            }
            return packResources;
        }, info, packType, Pack.Position.TOP, fixedPosition, PackSource.BUILT_IN);
    }
}
