package fuzs.puzzleslib.impl.core;

import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;

public interface ClientProxyImpl extends ProxyImpl {

    @Override
    default Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    default Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    default ClientPacketListener getClientPacketListener() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(connection, "client packet listener is null");
        return connection;
    }

    @Override
    default boolean hasControlDown() {
        return Screen.hasControlDown();
    }

    @Override
    default boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    default boolean hasAltDown() {
        return Screen.hasAltDown();
    }

    @Override
    default Component getKeyMappingComponent(String identifier) {
        return KeyMapping.createNameSupplier(identifier).get();
    }
}
