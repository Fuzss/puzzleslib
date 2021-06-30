package fuzs.puzzleslib.util;

import fuzs.puzzleslib.PuzzlesLib;
import com.google.common.base.CaseFormat;
import net.minecraft.util.ResourceLocation;
import net.minecraftforge.fml.ModLoadingContext;

/**
 * helper for namespace related things
 */
public class NamespaceUtil {

    /**
     * get active modid so entries can still be associated with the mod
     * @return active modid
     */
    public static String getActiveNamespace() {

        String namespace = ModLoadingContext.get().getActiveNamespace();
        if (namespace.equals("minecraft")) {

            PuzzlesLib.LOGGER.warn("minecraft ius active namespace");
        }

        return namespace;
    }

    /**
     * @param path path to create location for
     * @return resource location for active modid
     */
    public static ResourceLocation getLocation(String path) {

        return new ResourceLocation(getActiveNamespace(), path);
    }

    /**
     * most useful for capabilities
     * @param name name to format
     * @return name formatted as upper camel
     */
    public static String format(String name) {

        return CaseFormat.LOWER_UNDERSCORE.to(CaseFormat.UPPER_CAMEL, name);
    }

}
