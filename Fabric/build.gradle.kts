import fuzs.multiloader.extension.packageName

plugins {
    id("fuzs.multiloader.multiloader-convention-plugins-fabric")
}

dependencies {
    modApi(sharedLibs.fabricapi.fabric)
    modApi(sharedLibs.forgeconfigapiport.fabric)
}

multiloader {
    modFile {
        packagePrefix.set("impl")
        library.set(true)
    }

    mixins {
        plugin.set("${project.group}.${project.packageName}.mixin.MixinConfigPluginFabricImpl")
        mixin(
            "AbstractHorseFabricMixin",
            "AnimalFabricMixin",
            "AnvilMenuFabricMixin",
            "AxeItemFabricMixin",
            "BlockEntityFabricMixin",
            "BlockFabricMixin",
            "BoneMealItemFabricMixin",
            "BowItemFabricMixin",
            "CatFabricMixin",
            "ChunkMapFabricMixin",
            "CrossbowItemFabricMixin",
            "EnchantedCountIncreaseFunctionFabricMixin",
            "EnchantmentHelperFabricMixin",
            "EnderManFabricMixin",
            "EntityFabricMixin",
            "ExperienceOrbFabricMixin",
            "ExplosionFabricMixin",
            "FarmBlockFabricMixin",
            "FoxBreedGoalFabricMixin",
            "GrindstoneMenu\$ResultSlotFabricMixin",
            "GrindstoneMenuFabricMixin",
            "ItemEntityFabricMixin",
            "LevelFabricMixin",
            "LivingEntityFabricMixin",
            "LootItemRandomChanceWithEnchantedBonusConditionFabricMixin",
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
            "RunAroundLikeCrazyGoalFabricMixin",
            "ServerEntityFabricMixin",
            "ServerLevelFabricMixin",
            "ServerPlayerFabricMixin",
            "StartAttackingFabricMixin",
            "ThrownEnderpearlFabricMixin",
            "WolfFabricMixin"
        )
        accessor("GrindstoneMenuFabricAccessor", "PackRepositoryFabricAccessor")
        clientMixin(
            "AbstractClientPlayerFabricMixin",
            "AbstractContainerScreenFabricMixin",
            "CameraFabricMixin",
            "ChatListenerFabricMixin",
            "ClientLevelFabricMixin",
            "ClientPacketListenerFabricMixin",
            "DebugScreenOverlayMixin",
            "EffectRenderingInventoryScreenFabricMixin",
            "EntityRendererFabricMixin",
            "FogRendererFabricMixin",
            "GameRendererFabricMixin",
            "GuiFabricMixin",
            "GuiGraphicsFabricMixin",
            "ItemInHandRendererFabricMixin",
            "ItemRendererFabricMixin",
            "KeyboardHandlerFabricMixin",
            "KeyMappingFabricMixin",
            "LivingEntityRendererFabricMixin",
            "LocalPlayerFabricMixin",
            "MinecraftFabricMixin",
            "ModelManagerFabricMixin",
            "MouseHandlerFabricMixin",
            "OptionsFabricMixin",
            "PackSelectionScreenFabricMixin",
            "PlayerRendererFabricMixin",
            "RemotePlayerMixin",
            "RenderBuffersFabricMixin",
            "ScreenEffectRendererFabricMixin",
            "ScreenFabricMixin",
            "SkullBlockRendererFabricMixin",
            "ToastComponentFabricMixin"
        )
        clientAccessor("AbstractContainerScreenFabricAccessor", "MultiPlayerGameModeFabricAccessor")
        serverMixin("MainServerFabricMixin")
    }
}
