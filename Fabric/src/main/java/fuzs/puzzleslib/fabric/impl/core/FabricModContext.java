package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.common.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.common.impl.core.ModContext;
import fuzs.puzzleslib.common.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayNetworking;
import net.fabricmc.fabric.api.networking.v1.PayloadTypeRegistry;
import net.fabricmc.fabric.api.networking.v1.ServerPlayNetworking;
import net.minecraft.client.Minecraft;
import net.minecraft.client.multiplayer.ClientPacketListener;
import net.minecraft.network.protocol.common.custom.BrandPayload;
import net.minecraft.network.protocol.common.custom.CustomPacketPayload;
import net.minecraft.server.level.ServerPlayer;

import java.util.Objects;

public final class FabricModContext extends ModContext {

    public FabricModContext(String modId) {
        super(modId);
    }

    @Override
    protected void setupHandshakePayload(String modId, CustomPacketPayload.Type<BrandPayload> payloadType) {
        PayloadTypeRegistry.serverboundPlay().register(payloadType, BrandPayload.STREAM_CODEC);
        PayloadTypeRegistry.clientboundPlay().register(payloadType, BrandPayload.STREAM_CODEC);
        FabricProxy.get().setupHandshakePayload(payloadType);
    }

    @Override
    protected boolean isPresentServerside(CustomPacketPayload.Type<BrandPayload> payloadType) {
        ClientPacketListener clientPacketListener = Minecraft.getInstance().getConnection();
        return clientPacketListener != null && ClientPlayNetworking.canSend(payloadType);
    }

    @Override
    protected boolean isPresentClientside(CustomPacketPayload.Type<BrandPayload> payloadType, ServerPlayer serverPlayer) {
        Objects.requireNonNull(serverPlayer, "server player is null");
        return ServerPlayNetworking.canSend(serverPlayer.connection, payloadType);
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
