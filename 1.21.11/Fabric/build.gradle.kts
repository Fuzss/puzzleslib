plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    modApi(libs.fabricapi.fabric)
    modApi(libs.forgeconfigapiport.fabric)
}

multiloader {
    modFile {
        packagePrefix.set("impl")
        library.set(true)
    }

    mixins {
        plugin.set("${project.group}.fabric.mixin.MixinConfigPluginFabricImpl")
        mixin(
            "AbstractHorseFabricMixin",
            "AnimalFabricMixin",
            "AnvilMenuFabricMixin",
            "BlockEntityFabricMixin",
            "BlockFabricMixin",
            "BoneMealItemFabricMixin",
            "BowItemFabricMixin",
            "CatFabricMixin",
            "ChunkMapFabricMixin",
            "CrossbowItemFabricMixin",
            "EnderManFabricMixin",
            "EntityFabricMixin",
            "ExperienceOrbFabricMixin",
            "FoxBreedGoalFabricMixin",
            "GrindstoneMenu\u0024ResultSlotFabricMixin",
            "GrindstoneMenuFabricMixin",
            "ItemEntityFabricMixin",
            "LivingEntityFabricMixin",
            "MagmaCubeFabricMixin",
            "MenuProviderWithDataFabricMixin",
            "MobFabricMixin",
            "MonsterFabricMixin",
            "NaturalSpawnerFabricMixin",
            "OcelotFabricMixin",
            "PackRepositoryFabricMixin",
            "ParrotFabricMixin",
            "PersistentEntitySectionManagerFabricMixin",
            "PlayerChunkSenderFabricMixin",
            "PlayerFabricMixin",
            "ProjectileFabricMixin",
            "ReloadableServerResourcesFabricMixin",
            "RunAroundLikeCrazyGoalFabricMixin",
            "ServerExplosionFabricMixin",
            "ServerLevelFabricMixin",
            "ServerPlayerFabricMixin",
            "StartAttackingFabricMixin",
            "ThrownEnderpearlFabricMixin",
            "WolfFabricMixin"
        )
        clientMixin(
            "AbstractClientPlayerFabricMixin",
            "AbstractContainerScreenFabricMixin",
            "CameraFabricMixin",
            "ChatListenerFabricMixin",
            "ClientLevelFabricMixin",
            "ClientPacketListenerFabricMixin",
            "EffectsInInventoryFabricMixin",
            "EntityRendererFabricMixin",
            "FogRendererFabricMixin",
            "GameRendererFabricMixin",
            "GuiGraphicsFabricMixin",
            "ItemInHandRendererFabricMixin",
            "KeyboardHandlerFabricMixin",
            "KeyMappingFabricMixin",
            "LevelRendererMixin",
            "LivingEntityRendererFabricMixin",
            "LocalPlayerFabricMixin",
            "MinecraftFabricMixin",
            "MouseHandlerFabricMixin",
            "OptionsFabricMixin",
            "PackSelectionScreenFabricMixin",
            "RenderBuffersFabricMixin",
            "ScreenEffectRendererFabricMixin",
            "SkullBlockRendererFabricMixin",
            "ToastComponentFabricMixin"
        )
        serverMixin("ServerMainFabricMixin")
    }
}
