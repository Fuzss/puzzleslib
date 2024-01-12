package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

public interface ProxyImpl extends Proxy {

    @Override
    default Player getClientPlayer() {
        throw new RuntimeException("Client player accessed for wrong side!");
    }

    @Override
    default Level getClientLevel() {
        throw new RuntimeException("Client level accessed for wrong side!");
    }

    @Override
    default ClientPacketListener getClientPacketListener() {
        throw new RuntimeException("Client connection accessed for wrong side!");
    }

    @Override
    default boolean hasControlDown() {
        return false;
    }

    @Override
    default boolean hasShiftDown() {
        return false;
    }

    @Override
    default boolean hasAltDown() {
        return false;
    }

    @Override
    default Component getKeyMappingComponent(String identifier) {
        throw new RuntimeException("Key mapping component accessed for wrong side!");
    }
}
