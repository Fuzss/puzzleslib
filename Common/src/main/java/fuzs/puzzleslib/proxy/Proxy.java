package fuzs.puzzleslib.proxy;

import fuzs.puzzleslib.core.CoreServices;
import fuzs.puzzleslib.core.DistTypeExecutor;
import fuzs.puzzleslib.network.NetworkHandler;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.entity.player.Player;

import java.util.function.Function;

/**
 * proxy base class for client and server implementations
 * mainly used for handling content not present on a physical server
 */
public interface Proxy {
    /**
     * sided proxy depending on physical side
     */
    @SuppressWarnings("Convert2MethodRef")
    Proxy INSTANCE = DistTypeExecutor.getForDistType(() -> CoreServices.FACTORIES.clientProxy(), () -> CoreServices.FACTORIES.serverProxy());

    /**
     * @return client player from Minecraft singleton when on physical client, otherwise null
     */
    Player getClientPlayer();

    /**
     * @return Minecraft singleton instance on physical client, otherwise null
     */
    Object getClientInstance();

    /**
     * @return current game server, null when not in a world
     */
    MinecraftServer getGameServer();

    /**
     * only used by Fabric implementation of {@link NetworkHandler}
     * @param channelName channel name
     * @param factory message factory when received
     */
    default void registerClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, Message> factory) {

    }

    /**
     * only used by Fabric implementation of {@link NetworkHandler}
     * @param channelName channel name
     * @param factory message factory when received
     */
    default void registerServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, Message> factory) {

    }

    /**
     * useful for item tooltips
     * @return is the control key (command on mac) pressed
     */
    boolean hasControlDown();

    /**
     * useful for item tooltips
     * @return is the shift key pressed
     */
    boolean hasShiftDown();

    /**
     * useful for item tooltips
     * @return is the alt key pressed
     */
    boolean hasAltDown();
}
