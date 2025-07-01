package fuzs.puzzleslib.impl.content;

import com.google.common.collect.Maps;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.entity.ServerEntityLevelEvents;
import fuzs.puzzleslib.api.event.v1.entity.player.PlayerCopyEvents;
import fuzs.puzzleslib.api.event.v1.server.ServerLifecycleEvents;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.core.Holder;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.TickTask;
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
 * <p>
 * Will not apply any changes in a production environment.
 */
public final class CommandOverrides {
    private static final String KEY_PLAYER_JOINED_WORLD = PuzzlesLibMod.id("joined_world").toString();
    private static final Map<CommandEnvironment, Collection<String>> COMMAND_OVERRIDES = Maps.newEnumMap(
            CommandEnvironment.class);

    private CommandOverrides() {
        // NO-OP
    }

    /**
     * Registers a command to run for when a world (not level) is first created.
     *
     * @param command       command to run
     * @param onlyDedicated run commands only on a dedicated server
     */
    public static void registerServerCommand(String command, boolean onlyDedicated) {
        CommandEnvironment commandEnvironment =
                onlyDedicated ? CommandEnvironment.DEDICATED_SERVER : CommandEnvironment.SERVER;
        COMMAND_OVERRIDES.computeIfAbsent(commandEnvironment, $ -> new LinkedHashSet<>()).add(command);
    }

    /**
     * Registers an <code>/effect</code> command to run for when a player joins the world for the very first time.
     * <p>
     * Players will be marked with a value retrievable via <code>/tag</code> to prevent running commands again
     * afterward.
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
        CommandEnvironment commandEnvironment =
                onlyDedicated ? CommandEnvironment.DEDICATED_PLAYER : CommandEnvironment.PLAYER;
        COMMAND_OVERRIDES.computeIfAbsent(commandEnvironment, $ -> new LinkedHashSet<>()).add(command);
    }

    @ApiStatus.Internal
    public static void registerEventHandlers() {
        ServerLifecycleEvents.STARTED.register((MinecraftServer minecraftServer) -> {
            if (minecraftServer.getWorldData().overworldData().getGameTime() == 0 && minecraftServer.getWorldData()
                    .isAllowCommands()) {
                executeCommandOverrides(minecraftServer,
                        CommandEnvironment.SERVER,
                        CommandEnvironment.DEDICATED_SERVER,
                        UnaryOperator.identity());
            }
        });
        ServerEntityLevelEvents.LOAD.register((Entity entity, ServerLevel serverLevel, boolean isFreshEntity) -> {
            // idea from Serilum's Starter Kit mod
            if (isFreshEntity && entity instanceof ServerPlayer serverPlayer && !serverPlayer.getTags()
                    .contains(KEY_PLAYER_JOINED_WORLD)) {
                serverPlayer.addTag(KEY_PLAYER_JOINED_WORLD);
                if (serverLevel.getServer().getWorldData().isAllowCommands()) {
                    serverLevel.getServer().schedule(new TickTask(serverLevel.getServer().getTickCount(), () -> {
                        String playerName = serverPlayer.getGameProfile().getName();
                        executeCommandOverrides(serverPlayer.getServer(),
                                CommandEnvironment.PLAYER,
                                CommandEnvironment.DEDICATED_PLAYER,
                                (String s) -> s.replaceAll("@[sp]", playerName));
                    }));
                }
            }
            return EventResult.PASS;
        });
        PlayerCopyEvents.COPY.register((ServerPlayer originalServerPlayer, ServerPlayer newServerPlayer, boolean originalStillAlive) -> {
            if (!originalStillAlive) originalServerPlayer.removeTag(KEY_PLAYER_JOINED_WORLD);
        });
    }

    private static void executeCommandOverrides(MinecraftServer minecraftServer, CommandEnvironment commandEnvironment, CommandEnvironment dedicatedCommandEnvironment, UnaryOperator<String> formatter) {
        for (String command : COMMAND_OVERRIDES.getOrDefault(commandEnvironment, Collections.emptySet())) {
            minecraftServer.getCommands()
                    .performPrefixedCommand(minecraftServer.createCommandSourceStack(), formatter.apply(command));
        }
        if (minecraftServer instanceof DedicatedServer) {
            for (String command : COMMAND_OVERRIDES.getOrDefault(dedicatedCommandEnvironment, Collections.emptySet())) {
                minecraftServer.getCommands()
                        .performPrefixedCommand(minecraftServer.createCommandSourceStack(), formatter.apply(command));
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
