package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v4.NetworkingHelper;
import fuzs.puzzleslib.impl.PuzzlesLibMod;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.network.protocol.configuration.ServerConfigurationPacketListener;
import net.minecraft.server.network.ConfigurationTask;

import java.util.function.Consumer;

public record ModListConfigurationTask(ServerConfigurationPacketListener listener) implements ConfigurationTask {
    public static final Type TYPE = new Type(PuzzlesLibMod.id("mod_list").toString());

    @Override
    public void start(Consumer<Packet<?>> packetConsumer) {
        CustomPacketPayload.Type<ClientboundModListMessage> payloadType = NetworkingHelper.getPayloadType(
                ClientboundModListMessage.class);
        if (NetworkingHelper.hasChannel(this.listener, payloadType)) {
            packetConsumer.accept(new ClientboundModListMessage(ModContext.getModList()).toPacket());
        }
        NetworkingHelper.finishConfigurationTask(this.listener, TYPE);
    }

    @Override
    public Type type() {
        return TYPE;
    }
}
