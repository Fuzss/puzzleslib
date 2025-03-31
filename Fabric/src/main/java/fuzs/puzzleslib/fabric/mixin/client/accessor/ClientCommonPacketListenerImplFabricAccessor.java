package fuzs.puzzleslib.fabric.mixin.client.accessor;

import net.minecraft.client.multiplayer.ClientCommonPacketListenerImpl;
import net.minecraft.network.Connection;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.gen.Accessor;

@Mixin(ClientCommonPacketListenerImpl.class)
public interface ClientCommonPacketListenerImplFabricAccessor {

    @Accessor("connection")
    Connection puzzleslib$getConnection();
}
