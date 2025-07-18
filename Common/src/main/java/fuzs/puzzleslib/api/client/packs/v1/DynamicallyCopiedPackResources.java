package fuzs.puzzleslib.api.client.packs.v1;

import com.mojang.blaze3d.platform.NativeImage;
import fuzs.puzzleslib.api.core.v1.context.PackRepositorySourcesContext;
import fuzs.puzzleslib.api.resources.v1.AbstractModPackResources;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.packs.PackType;
import net.minecraft.server.packs.VanillaPackResources;
import net.minecraft.server.packs.resources.IoSupplier;
import net.minecraft.server.packs.resources.Resource;
import net.minecraft.server.packs.resources.ResourceManager;
import org.jetbrains.annotations.Nullable;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * A {@link net.minecraft.server.packs.PackResources} implementation for copying existing texture resources at runtime,
 * mainly in an effort to handle changed aspect ratios in entity textures with custom models.
 * <p>
 * Generally the implementation will try to pick the same texture the vanilla pack handler would (meaning the texture
 * from the top-most pack). But if the aspect ratio of that texture doesn't match the expected ratio, the original
 * texture from {@link VanillaPackResources} will be used.
 * <p>
 * To be registered via
 * {@link
 * fuzs.puzzleslib.api.client.core.v1.ClientModConstructor#onAddResourcePackFinders(PackRepositorySourcesContext)} and
 * {@link fuzs.puzzleslib.api.resources.v1.PackResourcesHelper}.
 */
public class DynamicallyCopiedPackResources extends AbstractModPackResources {
    private final ResourceManager resourceManager;
    private final VanillaPackResources vanillaPackResources;
    private final Map<ResourceLocation, TextureCopy> textures;

    protected DynamicallyCopiedPackResources(TextureCopy... textures) {
        Minecraft minecraft = Minecraft.getInstance();
        this.resourceManager = minecraft.getResourceManager();
        this.vanillaPackResources = minecraft.getVanillaPackResources();
        this.textures = Stream.of(textures)
                .collect(Collectors.toMap(TextureCopy::destinationLocation, Function.identity()));
    }

    @Nullable
    @Override
    public IoSupplier<InputStream> getResource(PackType packType, ResourceLocation resourceLocation) {
        if (this.textures.containsKey(resourceLocation)) {
            TextureCopy textureCopy = this.textures.get(resourceLocation);
            Optional<Resource> vanillaResource = this.resourceManager.getResource(textureCopy.vanillaLocation());
            if (vanillaResource.isPresent()) {
                try (NativeImage nativeImage = NativeImage.read(vanillaResource.get().open())) {
                    // check the vanilla texture aspect ratio; some mods using OptiFine change the texture file completely since they also change the model
                    // make sure to check the aspect ratio instead of absolute width / height to support higher resolution resource packs
                    // in that case fall back to the skeleton texture from the vanilla assets pack
                    if (nativeImage.getWidth() / nativeImage.getHeight() !=
                            textureCopy.vanillaImageWidth() / textureCopy.vanillaImageHeight()) {
                        return this.vanillaPackResources.getResource(packType, textureCopy.vanillaLocation());
                    }
                } catch (IOException exception) {
                    // NO-OP
                }
                return vanillaResource.get()::open;
            }
        }

        return null;
    }

    @Override
    public Set<String> getNamespaces(PackType packType) {
        return this.textures.keySet().stream().map(ResourceLocation::getNamespace).collect(Collectors.toSet());
    }

    /**
     * Provides a mod pack resources supplier for the defined data.
     *
     * @param textures texture copy data
     * @return mod pack resources supplier
     */
    public static Supplier<AbstractModPackResources> create(TextureCopy... textures) {
        return () -> {
            return new DynamicallyCopiedPackResources(textures);
        };
    }

    /**
     * Data class for texture copy information.
     *
     * @param vanillaLocation     location of the vanilla texture to copy from
     * @param destinationLocation location to copy to which will be used by the mod
     * @param vanillaImageWidth   width of the vanilla texture, might no longer match when overriden via resource packs
     * @param vanillaImageHeight  height of the vanilla texture, might no longer match when overriden via resource
     *                            packs
     */
    public record TextureCopy(ResourceLocation vanillaLocation,
                              ResourceLocation destinationLocation,
                              int vanillaImageWidth,
                              int vanillaImageHeight) {

        public TextureCopy {
            if (vanillaLocation.getNamespace().equals(destinationLocation.getNamespace())) {
                throw new IllegalStateException("%s and %s share same namespace".formatted(vanillaLocation,
                        destinationLocation
                ));
            }
            if (!vanillaLocation.getPath().endsWith(".png")) {
                throw new IllegalArgumentException("%s is no texture location".formatted(vanillaLocation));
            }
            if (!destinationLocation.getPath().endsWith(".png")) {
                throw new IllegalArgumentException("%s is no texture location".formatted(destinationLocation));
            }
        }
    }
}
