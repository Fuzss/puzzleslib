package fuzs.puzzleslib.api.core.v1.context;

import com.mojang.brigadier.CommandDispatcher;
import net.minecraft.commands.CommandBuildContext;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;

/**
 * register a new command, also supports replacing existing commands by default
 *
 * @param dispatcher  the dispatcher used for registering commands
 * @param environment command selection environment
 * @param context     registry access context
 */
public record RegisterCommandsContext(CommandDispatcher<CommandSourceStack> dispatcher, CommandBuildContext context,
                                      Commands.CommandSelection environment) {

}
