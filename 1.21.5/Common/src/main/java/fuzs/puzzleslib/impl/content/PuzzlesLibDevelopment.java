package fuzs.puzzleslib.impl.content;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameRules;

public class PuzzlesLibDevelopment implements ModConstructor {

    @Override
    public void onCommonSetup() {
        CommandOverrides.registerEventHandlers();
        initializeGameRules();
        initializeCommands();
    }

    private static void initializeCommands() {
        CommandOverrides.registerServerCommand("time set 4000", false);
        CommandOverrides.registerPlayerCommand("op @s", true);
        CommandOverrides.registerEffectCommand(MobEffects.NIGHT_VISION);
        CommandOverrides.registerEffectCommand(MobEffects.RESISTANCE);
        CommandOverrides.registerEffectCommand(MobEffects.FIRE_RESISTANCE);
        CommandOverrides.registerEffectCommand(MobEffects.STRENGTH);
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
}
