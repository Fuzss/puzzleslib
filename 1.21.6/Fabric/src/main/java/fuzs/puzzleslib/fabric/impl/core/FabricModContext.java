package fuzs.puzzleslib.fabric.impl.core;

import fuzs.puzzleslib.api.config.v3.ConfigHolder;
import fuzs.puzzleslib.api.init.v3.registry.RegistryManager;
import fuzs.puzzleslib.fabric.impl.config.FabricConfigHolderImpl;
import fuzs.puzzleslib.fabric.impl.init.FabricRegistryManager;
import fuzs.puzzleslib.impl.core.ModContext;
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
    public ConfigHolder.Builder getConfigHolder() {
        return this.addBuildable(new FabricConfigHolderImpl(this.modId));
    }

    @Override
    public RegistryManager getRegistryManager() {
        if (this.registryManager == null) {
            this.registryManager = new FabricRegistryManager(this.modId);
        }
        return this.registryManager;
    }
}
