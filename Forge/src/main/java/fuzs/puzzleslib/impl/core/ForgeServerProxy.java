package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import fuzs.puzzleslib.api.network.v3.ServerboundMessage;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.server.ServerLifecycleHooks;

import java.util.Objects;
import java.util.function.Supplier;

public class ForgeServerProxy implements ForgeProxy {

    @Override
    public Player getClientPlayer() {
        return null;
    }

    @Override
    public Level getClientLevel() {
        return null;
    }

    @Override
    public Object getClientInstance() {
        return null;
    }

    @Override
    public Connection getClientConnection() {
        return null;
    }

    @Override
    public MinecraftServer getGameServer() {
        return ServerLifecycleHooks.getCurrentServer();
    }

    @Override
    public boolean hasControlDown() {
        return false;
    }

    @Override
    public boolean hasShiftDown() {
        return false;
    }

    @Override
    public boolean hasAltDown() {
        return false;
    }

    @Override
    public Component getKeyMappingComponent(String identifier) {
        return Component.empty();
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, Supplier<NetworkEvent.Context> supplier) {

    }

    @Override
    public <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(T message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            ServerPlayer player = context.getSender();
            Objects.requireNonNull(player, "player is null");
            message.getHandler().handle(message, this.getGameServer(), player.connection, player, player.getLevel());
        });
        context.setPacketHandled(true);
    }
}
