import fuzs.multiloader.extension.packageName

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

dependencies {
    modCompileOnlyApi(sharedLibs.forgeconfigapiport.common)
}

multiloader {
    mixins {
        plugin.set("${project.group}.${project.packageName}.mixin.MixinConfigPluginImpl")
        mixin("DataCommandsMixin", "EnchantCommandMixin", "TagsProviderMixin")
        clientMixin("ClientSuggestionProviderMixin", "EditBoxMixin", "MinecraftMixin")
        serverMixin("DedicatedServerSettingsMixin", "EulaMixin")
    }
}
