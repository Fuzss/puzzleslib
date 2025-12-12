package fuzs.puzzleslib.api.item.v2;

import com.mojang.brigadier.exceptions.CommandSyntaxException;
import fuzs.puzzleslib.impl.PuzzlesLib;
import net.minecraft.commands.CommandSource;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.arguments.item.ItemInput;
import net.minecraft.network.chat.Component;
import net.minecraft.server.commands.GiveCommand;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.permissions.LevelBasedPermissionSet;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec2;
import net.minecraft.world.phys.Vec3;

import java.util.Collection;
import java.util.Collections;

/**
 * A helper class for giving items to the player just like the {@code /give} command does, meaning the item is either
 * added to the player inventory, or dropped on the ground if no available inventory space is found.
 */
public final class GiveItemHelper {

    private GiveItemHelper() {
        // NO-OP
    }

    /**
     * Gives an {@link ItemStack} to a {@link ServerPlayer} using the same mechanics as {@link GiveCommand}.
     *
     * @param itemStack    the item stack to give to the player
     * @param serverPlayer the player to give the item stack to
     */
    public static void giveItem(ItemStack itemStack, ServerPlayer serverPlayer) {
        giveItem(itemStack, Collections.singleton(serverPlayer));
    }

    /**
     * Gives an {@link ItemStack} to some {@link ServerPlayer ServerPlayers} using the same mechanics as
     * {@link GiveCommand}.
     *
     * @param itemStack     the item stack to give to the players
     * @param serverPlayers the players to give the item stack to
     */
    public static void giveItem(ItemStack itemStack, Collection<ServerPlayer> serverPlayers) {
        ServerPlayer serverPlayer = serverPlayers.iterator().next();
        giveItem(createTemporaryCommandSource(serverPlayer.level()), itemStack, serverPlayers);
    }

    private static void giveItem(CommandSourceStack commandSourceStack, ItemStack itemStack, Collection<ServerPlayer> serverPlayers) {
        try {
            ItemInput itemInput = new ItemInput(itemStack.getItemHolder(), itemStack.getComponentsPatch());
            GiveCommand.giveItem(commandSourceStack, itemInput, serverPlayers, itemStack.getCount());
        } catch (CommandSyntaxException exception) {
            PuzzlesLib.LOGGER.warn("Failed to give {} to players {}", itemStack, serverPlayers, exception);
        }
    }

    private static CommandSourceStack createTemporaryCommandSource(ServerLevel serverLevel) {
        return new CommandSourceStack(CommandSource.NULL,
                Vec3.ZERO,
                Vec2.ZERO,
                serverLevel,
                LevelBasedPermissionSet.GAMEMASTER,
                "Empty",
                Component.literal("Empty"),
                serverLevel.getServer(),
                null).withSuppressedOutput();
    }
}
