package fuzs.puzzleslib.api.core.v1;

import fuzs.puzzleslib.impl.core.CommonFactories;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

/**
 * Proxy base class for client and server implementations mainly used for handling content not present on a physical
 * server.
 */
public interface Proxy {
    Proxy INSTANCE = ModLoaderEnvironment.INSTANCE.isClient() ?
            CommonFactories.INSTANCE.getClientProxy() :
            CommonFactories.INSTANCE.getServerProxy();

    /**
     * @return client player from Minecraft singleton when on physical client, otherwise null
     */
    Player getClientPlayer();

    /**
     * @return client level from Minecraft singleton when on physical client, otherwise null
     */
    Level getClientLevel();

    /**
     * @return the connection to the server on physical client, otherwise null
     */
    ClientPacketListener getClientPacketListener();

    /**
     * Used to check if the control key (command on Mac) is pressed, useful for item tooltips.
     * <p>
     * Always returns <code>false</code> on the server side.
     */
    boolean hasControlDown();

    /**
     * Used to check if the shift key is pressed, useful for item tooltips.
     * <p>
     * Always returns <code>false</code> on the server side.
     *
     * @return is the shift key pressed
     */
    boolean hasShiftDown();

    /**
     * Used to check if the alt key is pressed, useful for item tooltips.
     * <p>
     * Always returns <code>false</code> on the server side.
     *
     * @return is the alt key pressed
     */
    boolean hasAltDown();

    /**
     * Split a text component into multiple parts depending on a predefined max width.
     * <p>
     * Most useful for constructing item tooltips in
     * {@link net.minecraft.world.item.Item#appendHoverText(ItemStack, Item.TooltipContext, List, TooltipFlag)}.
     * <p>
     * Always returns the unmodified component on the server side.
     *
     * @param component the component to split
     * @return the split component
     */
    List<Component> splitTooltipLines(Component component);
}
