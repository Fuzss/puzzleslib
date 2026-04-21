plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.forgeconfigapiport.common)
}

multiloader {
    mixins {
        plugin.set("${project.group}.mixin.MixinConfigPluginImpl")
        mixin(
            "DataCommandsMixin",
            "EnchantCommandMixin"
        )
        clientMixin(
            "ClientSuggestionProviderMixin",
            "EditBoxMixin",
            "MinecraftMixin"
        )
        serverMixin(
            "DedicatedServerSettingsMixin",
            "EulaMixin"
        )
    }
}
