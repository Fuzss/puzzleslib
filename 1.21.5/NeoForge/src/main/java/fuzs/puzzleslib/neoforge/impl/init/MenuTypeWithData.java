package fuzs.puzzleslib.neoforge.impl.init;

import fuzs.puzzleslib.api.init.v3.registry.MenuSupplierWithData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.network.codec.StreamCodec;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.neoforged.neoforge.network.IContainerFactory;

import java.util.Objects;

/**
 * Similar to Fabric's {@code net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType}.
 */
public final class MenuTypeWithData<T extends AbstractContainerMenu, S> extends MenuType<T> {
    private final StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec;

    public MenuTypeWithData(MenuSupplierWithData<T, S> menuSupplier, StreamCodec<? super RegistryFriendlyByteBuf, S> streamCodec) {
        super((IContainerFactory<T>) (int containerId, Inventory inventory, RegistryFriendlyByteBuf buf) -> {
            return menuSupplier.create(containerId, inventory, streamCodec.decode(buf));
        }, FeatureFlags.DEFAULT_FLAGS);
        Objects.requireNonNull(menuSupplier, "menu supplier is null");
        Objects.requireNonNull(streamCodec, "stream codec is null");
        this.streamCodec = streamCodec;
    }

    @Override
    public T create(int containerId, Inventory inventory) {
        throw new UnsupportedOperationException();
    }

    public StreamCodec<? super RegistryFriendlyByteBuf, S> getStreamCodec() {
        return this.streamCodec;
    }

    @SuppressWarnings("unchecked")
    public static <T> void encodeMenuData(AbstractContainerMenu containerMenu, RegistryFriendlyByteBuf buf, T data) {
        Objects.requireNonNull(containerMenu, "container menu is null");
        if (containerMenu.getType() instanceof MenuTypeWithData<?, ?> menuType) {
            ((MenuTypeWithData<?, T>) menuType).getStreamCodec().encode(buf, data);
        } else {
            throw new IllegalArgumentException("Menu type " + containerMenu.getType() + " does not support data");
        }
    }
}
