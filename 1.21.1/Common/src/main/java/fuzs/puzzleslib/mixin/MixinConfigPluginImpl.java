package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

public class MixinConfigPluginImpl implements IMixinConfigPlugin {
    private static final Collection<String> DEVELOPMENT_MIXINS = Set.of("client.EditBoxMixin",
            "server.DedicatedServerSettingsMixin", "server.EulaMixin"
    );

    @Override
    public void onLoad(String mixinPackage) {
        // make sure service providers are loaded using the Knot / FML class loader
        // see here for more information: https://github.com/Fuzss/puzzleslib/issues/41
        try {
            Class.forName("fuzs.puzzleslib.api.core.v1.ServiceProviderHelper");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
        // we print the mod list to the log as early as possible
        // this greatly helps with diagnosing issues where only a log has been provided and should otherwise be unobtrusive
        printModList();
    }

    private static void printModList() {
        if (ModLoaderEnvironment.INSTANCE.getModLoader().isFabricLike()) return;
        Collection<ModContainer> mods = ModLoaderEnvironment.INSTANCE.getModList().values();
        PuzzlesLib.LOGGER.info(dumpModList(mods));
    }

    private static String dumpModList(Collection<ModContainer> mods) {
        StringBuilder builder = new StringBuilder();
        builder.append("Loading ");
        builder.append(mods.size());
        builder.append(" mod");
        if (mods.size() != 1) builder.append("s");
        builder.append(":");
        for (ModContainer mod : mods) {
            if (mod.getParent() == null) {
                printMod(builder, mod, 0, false);
            }
        }
        return builder.toString();
    }

    private static void printMod(StringBuilder builder, ModContainer mod, int depth, boolean lastChild) {
        builder.append('\n');
        builder.append("\t".repeat(depth + 1));
        builder.append(depth == 0 ? "-" : (lastChild ? "\\" : "|") + "--");
        builder.append(' ');
        builder.append(mod.getModId());
        builder.append(' ');
        builder.append(mod.getVersion());
        Iterator<ModContainer> iterator = mod.getChildren().iterator();
        while (iterator.hasNext()) {
            ModContainer childMod = iterator.next();
            printMod(builder, childMod, depth + 1, !iterator.hasNext());
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return PuzzlesLib.isDevelopmentEnvironment() || !DEVELOPMENT_MIXINS.contains(
                mixinClassName.replaceAll(".+\\.mixin\\.", ""));
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {
        // NO-OP
    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // NO-OP
    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {
        // NO-OP
    }
}
