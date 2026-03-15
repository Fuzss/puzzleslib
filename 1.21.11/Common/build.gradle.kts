plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(libs.forgeconfigapiport.common)
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
