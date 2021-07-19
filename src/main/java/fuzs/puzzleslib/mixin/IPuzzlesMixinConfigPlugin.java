package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.PuzzlesLib;
import net.minecraftforge.fml.loading.FMLEnvironment;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.io.*;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * mixin config plugin implementation for discovering all mixin classes on it's own
 * also checks if mixins can actually be applied, only really suppresses a warning though
 */
public interface IPuzzlesMixinConfigPlugin extends IMixinConfigPlugin {

    @Override
    default void onLoad(String mixinPackage) {

    }

    @Override
    default String getRefMapperConfig() {

        return null;
    }

    @Override
    default boolean shouldApplyMixin(String targetClassName, String mixinClassName) {

        try {

            // will throw an exception when class is not found
            Class.forName(targetClassName);
            return true;
        } catch (ClassNotFoundException ignored) {

        }

        return false;
    }

    @Override
    default void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    default List<String> getMixins() {

        List<String> mixinClasses = getAllClasses(this.getClass().getClassLoader(), this.getClass().getPackage().getName(), "");
        PuzzlesLib.LOGGER.info("pre remove {}", mixinClasses);
        // remove self
        mixinClasses.removeIf(s -> s.equals(this.getClass().getSimpleName()));
        // mixin classes are sorted into sub-packages depending on dist compatibility
        if (FMLEnvironment.dist.isClient()) {

            mixinClasses.removeIf(s -> s.startsWith("server"));
        } else if (FMLEnvironment.dist.isDedicatedServer()) {

            mixinClasses.removeIf(s -> s.startsWith("client"));
        }

        PuzzlesLib.LOGGER.info("post remove {}", mixinClasses);
        return mixinClasses;
    }

    @Override
    default void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    default void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    static List<String> getAllClasses(ClassLoader loader, final String pack, final String subpack) {

        PuzzlesLib.LOGGER.info("{}, {}, {}", loader, pack, subpack);
        List<String> mixinClasses = new ArrayList<>();
        String currentPack = subpack.isEmpty() ? pack : pack.concat("/" + subpack);
        URL upackage = loader.getResource(currentPack.replaceAll("[.]", "/"));
        try {

            assert upackage != null;
            BufferedReader reader = new BufferedReader(new InputStreamReader((InputStream) upackage.getContent()));
            String line;
            while ((line = reader.readLine()) != null) {

                if (line.endsWith(".class")) {

                    line = line.substring(0, line.length() - ".class".length());
                    mixinClasses.add(subpack.isEmpty() ? line : subpack.concat("." + line));
                    continue;
                }

                if (new File(upackage.getPath() + File.separator + line).isDirectory()) {

                    mixinClasses.addAll(getAllClasses(loader, pack, subpack.isEmpty() ? line : subpack.concat("." + line)));
                }
            }

        } catch (IOException ignored) {

        }

        return mixinClasses;
    }

}
