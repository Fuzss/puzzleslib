package com.fuzs.puzzleslib.proxy;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.concurrent.RecursiveEventLoop;
import net.minecraftforge.fml.DistExecutor;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * @param <T> Minecraft client or server class
 */
public interface IProxy<T extends RecursiveEventLoop<?>> {

    /**
     * @return proxy for current physical side
     */
    static IProxy<?> getProxy() {

        return DistExecutor.safeRunForDist(() -> ClientProxy::new, () -> ServerProxy::new);
    }

    /**
     * @return Minecraft client or server instance
     */
    T getInstance();

    /**
     * @return player entity depending on side
     */
    @Nonnull
    PlayerEntity getPlayer(@Nullable PlayerEntity player);

}
