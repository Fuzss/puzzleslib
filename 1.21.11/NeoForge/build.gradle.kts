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

tasks.withType<JavaCompile>().configureEach {
    options.isDeprecation = false
    options.compilerArgs.clear()
}
