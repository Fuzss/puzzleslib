package fuzs.puzzleslib.mixin;

import com.google.common.collect.ImmutableSet;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.impl.PuzzlesLib;
import org.objectweb.asm.tree.ClassNode;
import org.spongepowered.asm.mixin.extensibility.IMixinConfigPlugin;
import org.spongepowered.asm.mixin.extensibility.IMixinInfo;

import java.util.Collection;
import java.util.List;
import java.util.Set;

public class MixinConfigPluginImpl implements IMixinConfigPlugin {
    private static final Collection<String> DEVELOPMENT_MIXINS = ImmutableSet.of("DataCommandsMixin",
            "EnchantCommandMixin",
            "client.ClientSuggestionProviderMixin",
            "client.EditBoxMixin",
            "server.DedicatedServerSettingsMixin",
            "server.EulaMixin");

    @Override
    public void onLoad(String mixinPackage) {
        // make sure service providers are loaded using the Knot / FML class loader
        // see here for more information: https://github.com/Fuzss/puzzleslib/issues/41
        try {
            Class.forName("fuzs.puzzleslib.api.core.v1.ServiceProviderHelper");
        } catch (ClassNotFoundException exception) {
            throw new RuntimeException(exception);
        }
    }

    @Override
    public String getRefMapperConfig() {
        return null;
    }

    @Override
    public boolean shouldApplyMixin(String targetClassName, String mixinClassName) {
        return ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment(PuzzlesLib.MOD_ID)
                || !DEVELOPMENT_MIXINS.contains(mixinClassName.replaceAll(".+\\.mixin\\.", ""));
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
