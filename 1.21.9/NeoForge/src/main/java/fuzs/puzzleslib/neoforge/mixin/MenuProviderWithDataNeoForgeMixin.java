package fuzs.puzzleslib.neoforge.mixin;

import fuzs.puzzleslib.api.container.v1.MenuProviderWithData;
import fuzs.puzzleslib.impl.event.EventImplHelper;
import fuzs.puzzleslib.neoforge.impl.init.MenuTypeWithData;
import net.minecraft.network.RegistryFriendlyByteBuf;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MenuProviderWithData.class)
public interface MenuProviderWithDataNeoForgeMixin<T> extends MenuProvider {

    @Shadow(remap = false)
    T getMenuData(@Nullable ServerPlayer serverPlayer);

    @Override
    default void writeClientSideData(AbstractContainerMenu containerMenu, RegistryFriendlyByteBuf buf) {
        Player player = EventImplHelper.getPlayerFromContainerMenu(containerMenu);
        MenuTypeWithData.encodeMenuData(containerMenu, buf, this.getMenuData((ServerPlayer) player));
    }
}
