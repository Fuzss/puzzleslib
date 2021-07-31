package fuzs.puzzleslib.proxy;

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
    @SuppressWarnings("Convert2MethodRef")
    static IProxy<?> getProxy() {

        return DistExecutor.unsafeRunForDist(() -> () -> ClientProxy.getInstance2(), () -> () -> ServerProxy.getInstance2());
    }

    /**
     * use {@link #getGameInstance()} instead
     * @return Minecraft client or server instance
     */
    @Deprecated
    default T getInstance() {

        return this.getGameInstance();
    }

    /**
     * @return Minecraft client or server instance
     */
    T getGameInstance();

    /**
     * @param player the player
     * @return player entity depending on side
     */
    @Nonnull
    PlayerEntity getPlayer(@Nullable PlayerEntity player);

}
