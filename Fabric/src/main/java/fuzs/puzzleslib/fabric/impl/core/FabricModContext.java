package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.capability.v3.CapabilityController;
import fuzs.puzzleslib.fabric.impl.capability.FabricCapabilityController;
import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.fabric.impl.network.FabricNetworkHandler;
import fuzs.puzzleslib.impl.config.ConfigHolderImpl;
import fuzs.puzzleslib.impl.core.ModContext;
import fuzs.puzzleslib.impl.init.RegistryManagerImpl;
import fuzs.puzzleslib.impl.network.NetworkHandlerRegistryImpl;
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
        PayloadTypeRegistry.playC2S().register(payloadType, BrandPayload.STREAM_CODEC);
        PayloadTypeRegistry.playS2C().register(payloadType, BrandPayload.STREAM_CODEC);
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
    protected NetworkHandlerRegistryImpl createNetworkHandler(String modId) {
        return new FabricNetworkHandler(modId);
    }

    @Override
    protected ConfigHolderImpl createConfigHolder(String modId) {
        return new FabricConfigHolderImpl(modId);
    }

    @Override
    protected RegistryManagerImpl createRegistryManager(String modId) {
        return new FabricRegistryManager(modId);
    }

    @Override
    protected CapabilityController createCapabilityController(String modId) {
        return new FabricCapabilityController(modId);
    }
}
