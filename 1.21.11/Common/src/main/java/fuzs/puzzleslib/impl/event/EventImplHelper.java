package fuzs.puzzleslib.impl.event;

import fuzs.puzzleslib.api.event.v1.entity.living.LivingJumpCallback;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
import fuzs.puzzleslib.impl.event.data.DefaultedDouble;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.GrindstoneMenu;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.phys.Vec3;
import org.jspecify.annotations.Nullable;

import java.util.Map;
import java.util.OptionalDouble;

public final class EventImplHelper {

    private EventImplHelper() {
        // NO-OP
    }

    public static void onLivingJump(LivingJumpCallback callback, LivingEntity entity) {
        Vec3 deltaMovement = entity.getDeltaMovement();
        DefaultedDouble jumpPower = DefaultedDouble.fromValue(deltaMovement.y);
        OptionalDouble newJumpPower;
        if (callback.onLivingJump(entity, jumpPower).isInterrupt()) {
            newJumpPower = OptionalDouble.of(0.0);
        } else {
            newJumpPower = jumpPower.getAsOptionalDouble();
        }

        if (newJumpPower.isPresent()) {
            entity.setDeltaMovement(deltaMovement.x, newJumpPower.getAsDouble(), deltaMovement.z);
        }
    }

    @Nullable
    public static Player getPlayerFromContainerMenu(AbstractContainerMenu abstractContainerMenu) {
        for (Slot slot : abstractContainerMenu.slots) {
            if (slot.container instanceof Inventory inventory) {
                return inventory.player;
            }
        }

        MinecraftServer minecraftServer = ProxyImpl.get().getMinecraftServer();
        if (minecraftServer != null) {
            for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
                if (serverPlayer.containerMenu == abstractContainerMenu) {
                    return serverPlayer;
                }
            }
        }

        return null;
    }

    public static Map.@Nullable Entry<GrindstoneMenu, Player> getGrindstoneMenuFromInputs(ItemStack primaryItemStack, ItemStack secondaryItemStack) {
        MinecraftServer minecraftServer = ProxyImpl.get().getMinecraftServer();
        if (minecraftServer != null) {
            for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
                if (serverPlayer.containerMenu instanceof GrindstoneMenu grindstoneMenu) {
                    if (grindstoneMenu.getSlot(0).getItem() == primaryItemStack
                            && grindstoneMenu.getSlot(1).getItem() == secondaryItemStack) {
                        return Map.entry(grindstoneMenu, serverPlayer);
                    }
                }
            }
        }

        return null;
    }
}
