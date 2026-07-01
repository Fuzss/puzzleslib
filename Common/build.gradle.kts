plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-common")
}

neoForge {
    // This breaks creating game artifacts which are required for looking up access level changes.
    // Since this project contains a lot of access level changes, it's better to have the setting like this.
//    validateAccessTransformers.set(false)
}

dependencies {
    compileOnlyApi(sharedLibs.forgeconfigapiport.common)
}

multiloader {
    mixins {
        plugin.set("${project.group}.mixin.MixinConfigPluginImpl")
        mixin("AbstractMinecartMixin", "DataCommandsMixin", "EnchantCommandMixin")
        clientMixin("ClientSuggestionProviderMixin", "EditBoxMixin", "ModelBakeryMixin")
        serverMixin("DedicatedServerSettingsMixin", "EulaMixin")
    }
}
