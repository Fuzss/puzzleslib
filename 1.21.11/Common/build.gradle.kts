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
            "EditBoxMixin"
        )
        serverMixin(
            "DedicatedServerSettingsMixin",
            "EulaMixin"
        )
    }
}

tasks.withType<JavaCompile>().configureEach {
    options.isDeprecation = false
    options.compilerArgs.clear()
}
