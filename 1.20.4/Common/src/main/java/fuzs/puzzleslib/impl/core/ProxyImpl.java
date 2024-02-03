package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.core.v1.Proxy;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.TooltipFlag;
import net.minecraft.world.level.Level;

import java.util.List;

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
    default List<Component> splitTooltipLines(Component component) {
        return List.of(component);
    }
}
