package fuzs.puzzleslib.impl;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.api.event.v1.LoadCompleteCallback;
import fuzs.puzzleslib.api.init.v3.override.CommandOverrides;
import fuzs.puzzleslib.api.init.v3.override.GameRuleValueOverrides;
import fuzs.puzzleslib.api.network.v3.NetworkHandler;
import fuzs.puzzleslib.impl.capability.ClientboundEntityCapabilityMessage;
import fuzs.puzzleslib.impl.core.ClientboundModListMessage;
import fuzs.puzzleslib.impl.core.EventHandlerProvider;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.event.core.EventInvokerImpl;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameRules;

/**
 * This has been separated from {@link PuzzlesLib} to prevent issues with static initialization when accessing constants
 * in {@link PuzzlesLib} early.
 */
public class PuzzlesLibMod extends PuzzlesLib implements ModConstructor {
    public static final NetworkHandler NETWORK = NetworkHandler.builder(MOD_ID)
            .optional()
            .registerClientbound(ClientboundEntityCapabilityMessage.class)
            .registerClientbound(ClientboundModListMessage.class);

    @Override
    public void onConstructMod() {
        registerEventHandlers();
        setupDevelopmentEnvironment();
    }

    private static void registerEventHandlers() {
        ModContext.registerEventHandlers();
        EventHandlerProvider.tryRegister(CommonAbstractions.INSTANCE);
        LoadCompleteCallback.EVENT.register(EventInvokerImpl::initialize);
    }

    private static void setupDevelopmentEnvironment() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironmentWithoutDataGeneration(PuzzlesLib.MOD_ID)) return;
        CommandOverrides.registerEventHandlers();
        initializeGameRules();
        initializeCommands();
    }

    private static void initializeCommands() {
        CommandOverrides.registerServerCommand("time set 4000", false);
        CommandOverrides.registerPlayerCommand("op @s", true);
        CommandOverrides.registerEffectCommand(MobEffects.NIGHT_VISION);
        CommandOverrides.registerEffectCommand(MobEffects.DAMAGE_RESISTANCE);
        CommandOverrides.registerEffectCommand(MobEffects.FIRE_RESISTANCE);
        CommandOverrides.registerEffectCommand(MobEffects.DAMAGE_BOOST);
        CommandOverrides.registerEffectCommand(MobEffects.WATER_BREATHING);
    }

    private static void initializeGameRules() {
        GameRuleValueOverrides.setValue(GameRules.RULE_DAYLIGHT, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_WEATHER_CYCLE, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_KEEPINVENTORY, true);
        GameRuleValueOverrides.setValue(GameRules.RULE_DOFIRETICK, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_MOBGRIEFING, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DOINSOMNIA, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DO_PATROL_SPAWNING, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DO_TRADER_SPAWNING, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_DO_VINES_SPREAD, false);
        GameRuleValueOverrides.setValue(GameRules.RULE_MAX_ENTITY_CRAMMING, 0);
        GameRuleValueOverrides.setValue(GameRules.RULE_PLAYERS_NETHER_PORTAL_DEFAULT_DELAY, 1);
        GameRuleValueOverrides.setValue(GameRules.RULE_COMMANDBLOCKOUTPUT, false);
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
