package fuzs.puzzleslib.proxy;

import fuzs.puzzleslib.network.Message;
import fuzs.puzzleslib.network.v2.ClientboundMessage;
import fuzs.puzzleslib.network.v2.ServerboundMessage;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;

import java.util.function.Function;

public interface FabricProxy extends Proxy {

    @Deprecated(forRemoval = true)
    <T extends Message<T>> void registerClientReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);

    @Deprecated(forRemoval = true)
    <T extends Message<T>> void registerServerReceiver(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);
    
    <T extends Record & ClientboundMessage<T>> void registerClientReceiverV2(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);

    <T extends Record & ServerboundMessage<T>> void registerServerReceiverV2(ResourceLocation channelName, Function<FriendlyByteBuf, T> factory);
}
