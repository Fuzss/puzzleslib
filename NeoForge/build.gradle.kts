plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

neoForge {
    validateAccessTransformers = false
}

multiloader {
    mixins {
        plugin.set("${project.group}.neoforge.mixin.MixinConfigPluginNeoForgeImpl")
        mixin("MenuProviderWithDataNeoForgeMixin")
        accessor("BiomeSpecialEffectsBuilderNeoForgeAccessor", "EntityNeoForgeAccessor", "PackNeoForgeAccessor")
        clientAccessor("RegisterKeyMappingsEventNeoForgeAccessor")
    }
}
