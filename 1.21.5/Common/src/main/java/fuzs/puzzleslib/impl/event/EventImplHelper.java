package fuzs.puzzleslib.impl.event;

import fuzs.puzzleslib.api.event.v1.data.DefaultedDouble;
import fuzs.puzzleslib.api.event.v1.entity.living.LivingJumpCallback;
import fuzs.puzzleslib.impl.core.proxy.ProxyImpl;
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

import java.util.Optional;
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

    public static Optional<Player> getGrindstoneUsingPlayer(ItemStack topInput, ItemStack bottomInput) {
        MinecraftServer minecraftServer = ProxyImpl.get().getMinecraftServer();
        Optional<Player> optional = Optional.empty();
        for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
            if (serverPlayer.containerMenu instanceof GrindstoneMenu menu) {
                optional = Optional.of(serverPlayer);
                if (menu.getSlot(0).getItem() == topInput && menu.getSlot(1).getItem() == bottomInput) {
                    break;
                }
            }
        }
        return optional;
    }

    public static Optional<Player> getPlayerFromContainerMenu(AbstractContainerMenu containerMenu) {
        for (Slot slot : containerMenu.slots) {
            if (slot.container instanceof Inventory inventory) {
                return Optional.of(inventory.player);
            }
        }
        MinecraftServer minecraftServer = ProxyImpl.get().getMinecraftServer();
        for (ServerPlayer serverPlayer : minecraftServer.getPlayerList().getPlayers()) {
            if (serverPlayer.containerMenu == containerMenu) {
                return Optional.of(serverPlayer);
            }
        }
        return Optional.empty();
    }
}
