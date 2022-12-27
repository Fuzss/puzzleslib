package fuzs.puzzleslib.network.v2;

import fuzs.puzzleslib.core.CommonFactories;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientLevel;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.client.player.LocalPlayer;

@SuppressWarnings("unused")
public record DummyMessage(int no) implements ClientboundMessage<DummyMessage> {

    @Override
    public ClientMessageListener<DummyMessage> getHandler() {
        return new ClientMessageListener<>() {
            @Override
            public void handle(DummyMessage message, Minecraft client, ClientPacketListener handler, LocalPlayer player, ClientLevel level) {
                System.out.println(message.no);
            }
        };
    }

    public static void main(String[] args) {
        CommonFactories.INSTANCE.networkV2("test").registerClientbound(DummyMessage.class);
    }
}
