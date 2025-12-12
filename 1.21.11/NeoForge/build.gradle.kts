plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

multiloader {
    mixins {
        plugin.set("${project.group}.neoforge.mixin.MixinConfigPluginNeoForgeImpl")
        mixin("MenuProviderWithDataNeoForgeMixin")
        accessor("BiomeSpecialEffectsBuilderNeoForgeAccessor")
        clientAccessor("BlockColorsNeoForgeAccessor", "RegisterKeyMappingsEventNeoForgeAccessor")
    }
}
