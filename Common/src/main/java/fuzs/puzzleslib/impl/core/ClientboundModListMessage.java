package fuzs.puzzleslib.impl.core;

import fuzs.puzzleslib.api.network.v4.message.MessageListener;
import fuzs.puzzleslib.api.network.v4.message.configuration.ClientboundConfigurationMessage;
import io.netty.buffer.ByteBuf;
import net.minecraft.network.codec.ByteBufCodecs;
import net.minecraft.network.codec.StreamCodec;

import java.util.Collection;
import java.util.HashSet;

public record ClientboundModListMessage(Collection<String> modList) implements ClientboundConfigurationMessage {
    public static final StreamCodec<ByteBuf, ClientboundModListMessage> STREAM_CODEC = ByteBufCodecs.STRING_UTF8.apply(
                    ByteBufCodecs.<ByteBuf, String, Collection<String>>collection(HashSet::new))
            .map(ClientboundModListMessage::new, ClientboundModListMessage::modList);

    @Override
    public MessageListener<Context> getListener() {
        return new MessageListener<>() {
            @Override
            public void accept(Context context) {
                ModContext.acceptServersideMods(ClientboundModListMessage.this.modList);
            }
        };
    }
}
