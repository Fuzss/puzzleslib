package fuzs.puzzleslib.api.resources.v1;

import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import net.minecraft.SharedConstants;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackResources;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.metadata.MetadataSectionSerializer;
import net.minecraft.server.packs.metadata.pack.PackMetadataSection;
import net.minecraft.server.packs.repository.Pack;
import net.minecraft.server.packs.repository.PackCompatibility;
import net.minecraft.server.packs.repository.PackSource;
import org.jetbrains.annotations.ApiStatus;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Collection;
import java.util.Collections;
import java.util.Optional;
import java.util.function.Predicate;
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
    private PackMetadataSection metadata;

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
    public InputStream getRootResource(String string) throws IOException {
        if ("pack.png".equals(string)) {
            Optional<Path> optional = ModLoaderEnvironment.INSTANCE.findModResource(this.logoModId, this.modLogoPath);
            if (optional.isPresent()) return Files.newInputStream(optional.get());
        }
        return null;
    }

    @Override
    public Collection<ResourceLocation> getResources(PackType packType, String string, String string2, int i, Predicate<String> predicate) {
        return Collections.emptyList();
    }

    @SuppressWarnings("unchecked")
    @Nullable
    @Override
    public <T> T getMetadataSection(MetadataSectionSerializer<T> deserializer) {
        return deserializer == PackMetadataSection.SERIALIZER ? (T) this.metadata : null;
    }

    @Override
    public String getName() {
        return this.id;
    }

    @Override
    public void close() {

    }

    @ApiStatus.Internal
    static Pack buildPack(PackType packType, Supplier<AbstractModPackResources> factory, String id, Component title, Component description, boolean required, boolean fixedPosition) {
        PackMetadataSection metadata = new PackMetadataSection(description, packType.getVersion(SharedConstants.getCurrentVersion()));
        return new Pack(id, required, () -> {
            AbstractModPackResources packResources = factory.get();
            packResources.id = id;
            packResources.metadata = metadata;
            if (packResources.logoModId == null) {
                packResources.logoModId = id;
            }
            return packResources;
        }, title, description, PackCompatibility.COMPATIBLE, Pack.Position.TOP, fixedPosition, PackSource.BUILT_IN);
    }
}
