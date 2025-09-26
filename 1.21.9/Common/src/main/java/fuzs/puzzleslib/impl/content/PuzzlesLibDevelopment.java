package fuzs.puzzleslib.impl.content;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.api.core.v1.utility.ResourceLocationHelper;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.GameRules;
import org.objectweb.asm.Type;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;

public class PuzzlesLibDevelopment extends PuzzlesLib implements ModConstructor {

    @Override
    public void onConstructMod() {
        CommandOverrides.registerEventHandlers();
    }

    @Override
    public void onCommonSetup() {
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

    public static void printClazzComponentsWithoutAccess(Class<?> clazz) {
        for (Field field : clazz.getDeclaredFields()) {
            if (!Modifier.isPublic(field.getModifiers()) && !field.isSynthetic()) {
                LOGGER.info("transitive-accessible\tfield\t{}\t{}\t{}",
                        Type.getInternalName(field.getDeclaringClass()),
                        field.getName(),
                        Type.getDescriptor(field.getType()));
            }
        }
        for (Method method : clazz.getDeclaredMethods()) {
            if (!Modifier.isPublic(method.getModifiers()) && !method.isSynthetic()) {
                LOGGER.info("transitive-accessible\tmethod\t{}\t{}\t{}",
                        Type.getInternalName(method.getDeclaringClass()),
                        method.getName(),
                        Type.getMethodDescriptor(method));
            }
        }
    }

    public static ResourceLocation id(String path) {
        return ResourceLocationHelper.fromNamespaceAndPath(MOD_ID, path);
    }
}
