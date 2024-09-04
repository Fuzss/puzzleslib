package fuzs.puzzleslib.api.init.v3.override;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.core.v1.ModLoaderEnvironment;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.dedicated.DedicatedServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.effect.MobEffect;
import net.minecraft.world.entity.Entity;
import org.jetbrains.annotations.ApiStatus;

import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.function.UnaryOperator;

/**
 * Allows for registering commands that run when a world or a player are first created in a development environment.
 * <p>Will not apply any changes in a production environment.
 */
public final class CommandOverrides {
    private static final String KEY_PLAYER_SEEN_WORLD = PuzzlesLibMod.id("has_seen_world").toLanguageKey();
    private static final Map<CommandEnvironment, Collection<String>> COMMAND_OVERRIDES = Maps.newEnumMap(
            CommandEnvironment.class);

    private CommandOverrides() {

    }

    /**
     * Registers a command to run for when a world (not level) is first created.
     *
     * @param command       command to run
     * @param onlyDedicated run commands only on a dedicated server
     */
    public static void registerServerCommand(String command, boolean onlyDedicated) {
        CommandEnvironment commandEnvironment = onlyDedicated ?
                CommandEnvironment.DEDICATED_SERVER :
                CommandEnvironment.SERVER;
        COMMAND_OVERRIDES.computeIfAbsent(commandEnvironment, $ -> new LinkedHashSet<>()).add(command);
    }

    /**
     * Registers an <code>/effect</code> command to run for when a player joins the world for the very first time.
     * <p>
     * Players will be marked with a value retrievable via <code>/tag</code> to prevent running commands a second time.
     *
     * @param holder mob effects to apply
     */
    public static void registerEffectCommand(Holder<MobEffect> holder) {
        registerPlayerCommand("effect give @s " + holder.getRegisteredName() + " infinite 127 true", false);
    }

    /**
     * Registers a command to run for when a player joins the world for the very first time.
     * <p>Players will be marked with a value retrievable via <code>/tag</code> to prevent running commands a second
     * time.
     *
     * @param command       command to run
     * @param onlyDedicated run commands only on a dedicated server
     */
    public static void registerPlayerCommand(String command, boolean onlyDedicated) {
        CommandEnvironment commandEnvironment = onlyDedicated ?
                CommandEnvironment.DEDICATED_PLAYER :
                CommandEnvironment.PLAYER;
        COMMAND_OVERRIDES.computeIfAbsent(commandEnvironment, $ -> new LinkedHashSet<>()).add(command);
    }

    @ApiStatus.Internal
    public static void registerHandlers() {
        if (!ModLoaderEnvironment.INSTANCE.isDevelopmentEnvironment()) return;
        ServerLifecycleEvents.STARTED.register((MinecraftServer server) -> {
            if (server.overworld().getGameTime() == 0) {
                executeCommandOverrides(server,
                        CommandEnvironment.SERVER,
                        CommandEnvironment.DEDICATED_SERVER,
                        UnaryOperator.identity()
                );
            }
        });
        ServerEntityLevelEvents.LOAD.register((Entity entity, ServerLevel level) -> {
            // idea from Serilum's Starter Kit mod
            if (entity instanceof ServerPlayer serverPlayer &&
                    !serverPlayer.getTags().contains(KEY_PLAYER_SEEN_WORLD)) {
                serverPlayer.addTag(KEY_PLAYER_SEEN_WORLD);
                String playerName = serverPlayer.getGameProfile().getName();
                executeCommandOverrides(serverPlayer.server,
                        CommandEnvironment.PLAYER,
                        CommandEnvironment.DEDICATED_PLAYER,
                        s -> s.replaceAll("@[sp]", playerName)
                );
            }
            return EventResult.PASS;
        });
    }

    private static void executeCommandOverrides(MinecraftServer server, CommandEnvironment commandEnvironment, CommandEnvironment dedicatedCommandEnvironment, UnaryOperator<String> formatter) {
        for (String command : COMMAND_OVERRIDES.getOrDefault(commandEnvironment, Collections.emptySet())) {
            server.getCommands().performPrefixedCommand(server.createCommandSourceStack(), formatter.apply(command));
        }
        if (server instanceof DedicatedServer) {
            for (String command : COMMAND_OVERRIDES.getOrDefault(dedicatedCommandEnvironment, Collections.emptySet())) {
                server.getCommands()
                        .performPrefixedCommand(server.createCommandSourceStack(), formatter.apply(command));
            }
        }
    }

    private enum CommandEnvironment {
        DEDICATED_SERVER,
        SERVER,
        DEDICATED_PLAYER,
        PLAYER
    }
}
