package fuzs.puzzleslib.impl.event;

import fuzs.puzzleslib.api.core.v1.CommonAbstractions;
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
import org.jetbrains.annotations.Nullable;

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

    public static Optional<Player> getGrindstoneUsingPlayer(ItemStack topInput, ItemStack bottomInput) {
        MinecraftServer minecraftServer = CommonAbstractions.INSTANCE.getMinecraftServer();
        Optional<Player> optional = Optional.empty();
        for (ServerPlayer player : minecraftServer.getPlayerList().getPlayers()) {
            if (player.containerMenu instanceof GrindstoneMenu menu) {
                optional = Optional.of(player);
                if (menu.getSlot(0).getItem() == topInput && menu.getSlot(1).getItem() == bottomInput) {
                    break;
                }
            }
        }
        return optional;
    }
}
