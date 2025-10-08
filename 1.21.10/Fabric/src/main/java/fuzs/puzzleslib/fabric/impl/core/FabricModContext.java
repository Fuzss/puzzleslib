package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
        PayloadTypeRegistry.playC2S().register(this.payloadType, BrandPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(this.payloadType, BrandPayload.STREAM_CODEC);
        FabricProxy.get().setupHandshakePayload(this.payloadType);
    }

    @Override
    public boolean isPresentServerside() {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        return clientPacketListener != null && ClientPlayNetworking.canSend(this.payloadType);
    }

    @Override
    public boolean isPresentClientside(ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return ServerPlayNetworking.canSend(serverPlayer.connection, this.payloadType);
    }

    @Override
    protected ConfigHolderImpl createConfigHolder(String modId) {
        return new FabricConfigHolderImpl(modId);
    }

    @Override
    protected RegistryManagerImpl createRegistryManager(String modId) {
        return new FabricRegistryManager(modId);
    }
}
