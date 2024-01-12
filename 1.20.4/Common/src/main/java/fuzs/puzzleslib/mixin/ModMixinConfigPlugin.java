package fuzs.puzzleslib.mixin;

import fuzs.puzzleslib.api.core.v1.ModContainer;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class ModMixinConfigPlugin implements IMixinConfigPlugin {

    @Override
    public void onLoad(String mixinPackage) {
        // make sure service providers are loaded using the Knot / FML class loader
        // see here for more information: https://github.com/Fuzss/puzzleslib/issues/41
        try {
            Class.forName("fuzs.puzzleslib.api.core.v1.ServiceProviderHelper");
        } catch (ClassNotFoundException e) {
            throw new RuntimeException(e);
        }
        // we print the mod list to the log as early as possible (would be even better as language provider,
        // but not sure how to include that in Puzzles Lib) just like Fabric Loader does
        // this greatly helps with diagnosing issues where only a log has been provided and should otherwise be unobtrusive
        printModList();
    }

    private static void printModList() {
        if (!ModLoaderEnvironment.INSTANCE.getModLoader().isForgeLike()) return;
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
            builder.append('\n');
            builder.append("\t");
            builder.append("-");
            builder.append(' ');
            builder.append(mod.getModId());
            builder.append(' ');
            builder.append(mod.getVersion());
        }
        return builder.toString();
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return true;
    }

    @Override
    public void acceptTargets(Set<String> myTargets, Set<String> otherTargets) {

    }

    @Override
    public List<String> getMixins() {
        return null;
    }

    @Override
    public void preApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }

    @Override
    public void postApply(String targetClassName, ClassNode targetClass, String mixinClassName, IMixinInfo mixinInfo) {

    }
}
