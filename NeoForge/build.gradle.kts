import fuzs.multiloader.extension.packageName

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-neoforge")
}

neoForge {
    // This breaks creating game artifacts which are required for looking up access level changes.
    // Since this project contains a lot of access level changes, it's better to have the setting like this.
    validateAccessTransformers.set(false)
}

multiloader {
    mixins {
        plugin.set("${project.group}.${project.packageName}.mixin.MixinConfigPluginNeoForgeImpl")
        mixin("MenuProviderWithDataNeoForgeMixin")
        accessor("BiomeSpecialEffectsBuilderNeoForgeAccessor", "EntityNeoForgeAccessor", "PackNeoForgeAccessor")
        clientMixin("MouseHandlerNeoForgeMixin")
        clientAccessor("RegisterKeyMappingsEventNeoForgeAccessor")
    }
}
