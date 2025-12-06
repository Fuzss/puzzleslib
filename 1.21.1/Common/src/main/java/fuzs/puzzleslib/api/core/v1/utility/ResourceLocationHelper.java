package fuzs.puzzleslib.api.core.v1.utility;

import net.minecraft.resources.ResourceLocation;
import org.jetbrains.annotations.Nullable;

/**
 * A utility class providing an abstraction for {@link ResourceLocation} creation changes in Minecraft 1.21+.
 */
@Deprecated
public final class ResourceLocationHelper {

    private ResourceLocationHelper() {
        // NO-OP
    }

    /**
     * Creates a new resource location from the provided namespace and path.
     *
     * @param namespace the namespace
     * @param path      the path
     * @return the new resource location
     */
    public static ResourceLocation fromNamespaceAndPath(String namespace, String path) {
        return ResourceLocation.fromNamespaceAndPath(namespace, path);
    }

    /**
     * Creates a new resource location from the provided path for the <code>minecraft</code> namespace.
     *
     * @param path the path
     * @return the new resource location
     */
    public static ResourceLocation withDefaultNamespace(String path) {
        return ResourceLocation.withDefaultNamespace(path);
    }

    /**
     * Creates a new resource location from parsing the provided input. Throws an exception when invalid characters are
     * present.
     *
     * @param location the location input
     * @return the new resource location
     */
    public static ResourceLocation parse(String location) {
        return ResourceLocation.parse(location);
    }

    /**
     * Creates a new resource location from parsing the provided input. Returns <code>null</code> when invalid
     * characters are present.
     *
     * @param location the location input
     * @return the new resource location or null
     */
    @Nullable
    public static ResourceLocation tryParse(String location) {
        return ResourceLocation.tryParse(location);
    }
}
