package fuzs.puzzleslib.impl.content;

import fuzs.puzzleslib.api.core.v1.ModConstructor;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.resources.Identifier;
import net.minecraft.world.effect.MobEffects;
import net.minecraft.world.level.gamerules.GameRules;
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
        GameRules.ADVANCE_TIME.defaultValue = Boolean.FALSE;
        GameRules.ADVANCE_TIME.defaultValue = Boolean.FALSE;
        GameRules.ADVANCE_WEATHER.defaultValue = Boolean.FALSE;
        GameRules.KEEP_INVENTORY.defaultValue = true;
        GameRules.FIRE_SPREAD_RADIUS_AROUND_PLAYER.defaultValue = 0;
        GameRules.MOB_GRIEFING.defaultValue = Boolean.FALSE;
        GameRules.SPAWN_PHANTOMS.defaultValue = Boolean.FALSE;
        GameRules.SPAWN_PATROLS.defaultValue = Boolean.FALSE;
        GameRules.SPAWN_WANDERING_TRADERS.defaultValue = Boolean.FALSE;
        GameRules.SPREAD_VINES.defaultValue = Boolean.FALSE;
        GameRules.MAX_ENTITY_CRAMMING.defaultValue = 0;
        GameRules.PLAYERS_NETHER_PORTAL_DEFAULT_DELAY.defaultValue = 1;
        GameRules.COMMAND_BLOCK_OUTPUT.defaultValue = Boolean.FALSE;
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

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }
}
