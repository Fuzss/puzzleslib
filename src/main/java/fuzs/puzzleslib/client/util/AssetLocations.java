package fuzs.puzzleslib.client.util;

import net.minecraft.util.ResourceLocation;

@SuppressWarnings("unused")
public class AssetLocations {

    public static ResourceLocation getBlockStatesPath(ResourceLocation location) {

        return new ResourceLocation(location.getNamespace(), "blockstates/" + location.getPath() + ".json");
    }

    public static ResourceLocation getBlockModelName(ResourceLocation location) {

        return new ResourceLocation(location.getNamespace(), "block/" + location.getPath());
    }

    public static ResourceLocation getBlockModelPath(ResourceLocation location) {

        return new ResourceLocation(location.getNamespace(), "models/" + location.getPath() + ".json");
    }

}
