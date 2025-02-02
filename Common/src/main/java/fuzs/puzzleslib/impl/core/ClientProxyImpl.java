package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.util.v1.ComponentHelper;
import fuzs.puzzleslib.api.client.gui.v2.components.tooltip.ClientComponentSplitter;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.List;
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
    default List<Component> splitTooltipLines(Component component) {
        return ClientComponentSplitter.splitTooltipLines(component).map(ComponentHelper::toComponent).toList();
    }
}
