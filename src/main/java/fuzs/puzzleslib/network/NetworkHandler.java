package fuzs.puzzleslib.network;

import fuzs.puzzleslib.PuzzlesLib;
import fuzs.puzzleslib.network.message.Message;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.entity.player.ServerPlayerEntity;
import net.minecraft.util.RegistryKey;
import net.minecraft.util.ResourceLocation;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import net.minecraftforge.fml.LogicalSide;
import net.minecraftforge.fml.network.NetworkEvent;
import net.minecraftforge.fml.network.NetworkRegistry;
import net.minecraftforge.fml.network.PacketDistributor;
import net.minecraftforge.fml.network.simple.SimpleChannel;

import javax.annotation.Nullable;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Supplier;

/**
 * handler for network communications of all puzzles lib mods
 */
@SuppressWarnings("unused")
public class NetworkHandler {

    /**
     * protocol version for testing client-server compatibility of this mod
     */
    private static final String PROTOCOL_VERSION = Integer.toString(1);
    /**
     * channel for sending messages
     */
    private static final SimpleChannel MAIN_CHANNEL = NetworkRegistry.newSimpleChannel(new ResourceLocation(PuzzlesLib.MODID, "main_channel"),
            () -> PROTOCOL_VERSION, PROTOCOL_VERSION::equals, PROTOCOL_VERSION::equals);
    /**
     * message index
     */
    private static final AtomicInteger DISCRIMINATOR = new AtomicInteger();

    /**
     * register a message for a side
     * @param supplier supplier for message (called when receiving at executing end)
     * @param executionSide side this message is to be executed at
     */
    public void registerMessage(Supplier<Message> supplier, LogicalSide executionSide) {

        MAIN_CHANNEL.registerMessage(DISCRIMINATOR.getAndIncrement(), supplier.get().getClass(), Message::write, buf -> supplier.get().getMessage(buf), (message, context) -> {

            NetworkEvent.Context ctx = context.get();
            assert executionSide == ctx.getDirection().getReceptionSide() : "Unable to handle " + message.getClass().getSimpleName() + " message: " + "Received at wrong side";

            PlayerEntity player = PuzzlesLib.getProxy().getPlayer(ctx.getSender());
            // https://stackoverflow.com/questions/15722184/method-in-is-defined-in-inaccessible-class-or-interface-compilation
            ctx.enqueueWork(() -> message.process(player));
            ctx.setPacketHandled(true);
        });
    }

    /**
     * send message from client to server
     * @param message message to send
     */
    public void sendToServer(Message message) {

        MAIN_CHANNEL.sendToServer(message);
    }

    /**
     * send message from server to client
     * @param message message to send
     * @param player client player to send to
     */
    public void sendTo(Message message, ServerPlayerEntity player) {

        MAIN_CHANNEL.send(PacketDistributor.PLAYER.with(() -> player), message);
    }

    /**
     * send message from server to all clients
     * @param message message to send
     */
    public void sendToAll(Message message) {

        MAIN_CHANNEL.send(PacketDistributor.ALL.noArg(), message);
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param world dimension key provider world
     * @param pos source position
     */
    public void sendToAllNear(Message message, World world, BlockPos pos) {

        this.sendToAllNearExcept(message, world, pos, null);
    }

    /**
     * send message from server to all clients near given position
     * @param message message to send
     * @param world dimension key provider world
     * @param pos source position
     * @param exclude exclude player having caused this event
     */
    public void sendToAllNearExcept(Message message, World world, BlockPos pos, @Nullable ServerPlayerEntity exclude) {

        PacketDistributor.TargetPoint targetPoint = new PacketDistributor.TargetPoint(exclude, pos.getX(), pos.getY(), pos.getZ(), 64.0D, world.dimension());
        MAIN_CHANNEL.send(PacketDistributor.NEAR.with(() -> targetPoint), message);
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param world dimension key provider world
     */
    public void sendToDimension(Message message, World world) {

        this.sendToDimension(message, world.dimension());
    }

    /**
     * send message from server to all clients in dimension
     * @param message message to send
     * @param dimension dimension to send message in
     */
    public void sendToDimension(Message message, RegistryKey<World> dimension) {

        MAIN_CHANNEL.send(PacketDistributor.DIMENSION.with(() -> dimension), message);
    }

}
