package com.fuzs.puzzleslib.network.message;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.network.PacketBuffer;

import java.util.function.Consumer;

/**
 * network message template
 */
public abstract class Message {

    /**
     * writes message data to buffer
     * @param buf network data byte buffer
     */
    public abstract void write(final PacketBuffer buf);

    /**
     * reads message data from buffer
     * @param buf network data byte buffer
     */
    public abstract void read(final PacketBuffer buf);

    /**
     * call {@link #read} and return this
     * @param buf network data byte buffer
     * @param <T> this
     * @return instance of this
     */
    @SuppressWarnings("unchecked")
    public final <T extends Message> T getMessage(PacketBuffer buf) {

        this.read(buf);
        return (T) this;
    }

    /**
     * handles message on receiving side
     * @param player server player when sent from client
     */
    public final void process(PlayerEntity player) {

        this.createProcessor().accept(player);
    }

    /**
     * @return message processor to run when received
     */
    protected abstract MessageProcessor createProcessor();

    /**
     * separate class for executing message when received to work around sided limitations
     */
    @FunctionalInterface
    protected interface MessageProcessor extends Consumer<PlayerEntity> {

    }

}
