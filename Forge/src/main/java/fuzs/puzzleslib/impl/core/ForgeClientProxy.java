package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.Connection;
import net.minecraft.network.chat.Component;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;
import net.minecraftforge.network.NetworkEvent;

import java.util.Objects;
import java.util.function.Supplier;

public class ForgeClientProxy extends ForgeServerProxy {

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    public Object getClientInstance() {
        return Minecraft.getInstance();
    }

    @Override
    public Connection getClientConnection() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(connection, "Cannot send packets when not in game!");
        return connection.getConnection();
    }

    @Override
    public boolean hasControlDown() {
        return Screen.hasControlDown();
    }

    @Override
    public boolean hasShiftDown() {
        return Screen.hasShiftDown();
    }

    @Override
    public boolean hasAltDown() {
        return Screen.hasAltDown();
    }

    @Override
    public Component getKeyMappingComponent(String identifier) {
        return KeyMapping.createNameSupplier(identifier).get();
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(T message, Supplier<NetworkEvent.Context> supplier) {
        NetworkEvent.Context context = supplier.get();
        context.enqueueWork(() -> {
            Minecraft minecraft = Minecraft.getInstance();
            LocalPlayer player = minecraft.player;
            Objects.requireNonNull(player, "player is null");
            message.getHandler().handle(message, minecraft, player.connection, player, minecraft.level);
        });
        context.setPacketHandled(true);
    }
}
