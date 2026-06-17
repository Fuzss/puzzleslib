import fuzs.multiloader.extension.packageName

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

multiloader {
    mixins {
        plugin.set("${project.group}.${project.packageName}.mixin.MixinConfigPluginNeoForgeImpl")
        mixin("MenuProviderWithDataNeoForgeMixin")
        accessor("BiomeSpecialEffectsBuilderNeoForgeAccessor", "EntityNeoForgeAccessor", "PackNeoForgeAccessor")
        clientMixin("MouseHandlerNeoForgeMixin")
        clientAccessor("BrandingControlAccessor", "RegisterKeyMappingsEventNeoForgeAccessor")
    }
}
