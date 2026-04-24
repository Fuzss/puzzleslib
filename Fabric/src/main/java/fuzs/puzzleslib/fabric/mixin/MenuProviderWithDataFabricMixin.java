package fuzs.puzzleslib.fabric.mixin;

import fuzs.puzzleslib.api.container.v1.MenuProviderWithData;
import net.fabricmc.fabric.api.menu.v1.ExtendedMenuProvider;
import net.minecraft.server.level.ServerPlayer;
import org.jspecify.annotations.Nullable;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;

@Mixin(MenuProviderWithData.class)
public interface MenuProviderWithDataFabricMixin<T> extends ExtendedMenuProvider<T> {

    @Shadow(remap = false)
    T getMenuData(@Nullable ServerPlayer serverPlayer);

    @Override
    default T getScreenOpeningData(ServerPlayer serverPlayer) {
        return this.getMenuData(serverPlayer);
    }
}
