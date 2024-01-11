package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v2.MessageV2;
import fuzs.puzzleslib.api.network.v3.ClientboundMessage;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PacketSender;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.level.Level;

import java.util.Objects;
import java.util.function.Function;

public class FabricClientProxy extends FabricServerProxy {

    @Override
    public Player getClientPlayer() {
        return Minecraft.getInstance().player;
    }

    @Override
    public Level getClientLevel() {
        return Minecraft.getInstance().level;
    }

    @Override
    public ClientPacketListener getClientPacketListener() {
        ClientPacketListener connection = Minecraft.getInstance().getConnection();
        Objects.requireNonNull(connection, "client packet listener is null");
        return connection;
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
    public <T extends MessageV2<T>> void registerLegacyClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, (Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            client.execute(() -> message.makeHandler().handle(message, client.player, client));
        });
    }

    @Override
    public <T extends Record & ClientboundMessage<T>> void registerClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory) {
        ClientPlayNetworking.registerGlobalReceiver(channelName, (Minecraft client, ClientPacketListener handler, FriendlyByteBuf buf, PacketSender responseSender) -> {
            T message = factory.apply(buf);
            client.execute(() -> {
                LocalPlayer player = client.player;
                Objects.requireNonNull(player, "player is null");
                message.getHandler().handle(message, client, handler, player, client.level);
            });
        });
    }
}
