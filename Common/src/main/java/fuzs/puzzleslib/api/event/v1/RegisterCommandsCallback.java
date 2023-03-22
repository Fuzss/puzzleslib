package fuzs.puzzleslib.api.event.v1;

import com.mojang.brigadier.CommandDispatcher;
import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

@FunctionalInterface
public interface RegisterCommandsCallback {
    EventInvoker<RegisterCommandsCallback> EVENT = EventInvoker.lookup(RegisterCommandsCallback.class);

    /**
     * Register a new command, also supports replacing existing commands by default.
     *
     * @param dispatcher  the dispatcher used for registering commands
     * @param environment command selection environment
     * @param context     registry access context
     */
    void onRegisterCommands(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context, Commands.CommandSelection environment);
}
