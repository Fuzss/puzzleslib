package fuzs.puzzleslib.api.entity.v1;

import fuzs.puzzleslib.impl.PuzzlesLib;
import fuzs.puzzleslib.impl.entity.ClientboundAddEntityDataMessage;
import io.netty.buffer.Unpooled;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.world.entity.Entity;

/**
 * An interface attached to {@link Entity} to send additional data to clients when the entity is added to the client level.
 * <p>Using this interface requires overriding {@link Entity#getAddEntityPacket()} and returning {@link #getPacket(Entity)}
 */
public interface AdditionalAddEntityData {

    /**
     * Create a custom packet to be returned from {@link Entity#getAddEntityPacket()}
     *
     * @param entity the entity to read data from
     * @param <T>    entity type
     * @return the vanilla packet to be sent
     */
    static <T extends Entity & AdditionalAddEntityData> Packet<ClientGamePacketListener> getPacket(T entity) {
        ClientboundAddEntityPacket vanillaPacket = new ClientboundAddEntityPacket(entity);
        FriendlyByteBuf buf = new FriendlyByteBuf(Unpooled.buffer());
        entity.writeAdditionalAddEntityData(buf);
        return PuzzlesLib.NETWORK.toClientboundPacket(new ClientboundAddEntityDataMessage(vanillaPacket, buf));
    }

    /**
     * Write additional entity data to a buffer.
     *
     * @param buf byte buffer to write to
     */
    void writeAdditionalAddEntityData(FriendlyByteBuf buf);

    /**
     * Read additional entity data from a buffer.
     *
     * @param buf byte buffer to read from
     */
    void readAdditionalAddEntityData(FriendlyByteBuf buf);
}
