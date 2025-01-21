package fuzs.puzzleslib.api.event.v1.entity;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import fuzs.puzzleslib.api.event.v1.data.MutableFloat;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.projectile.ThrownEnderpearl;
import net.minecraft.world.phys.HitResult;
import net.minecraft.world.phys.Vec3;

public interface EnderPearlTeleportCallback {
    EventInvoker<EnderPearlTeleportCallback> EVENT = EventInvoker.lookup(EnderPearlTeleportCallback.class);

    /**
     * Fires when an ender pearl lands and is about to teleport the player that threw it.
     *
     * @param serverPlayer     the player teleporting
     * @param targetPosition   the position the player is teleporting to
     * @param thrownEnderpearl the thrown ender pearl entity
     * @param damageAmount     the damage amount the player is about to receive upon teleporting
     * @param hitResult        the hit result for the ender pearl entity
     * @return <ul>
     *         <li>{@link EventResult#INTERRUPT INTERRUPT} to disallow the teleport to happen</li>
     *         <li>{@link EventResult#PASS PASS} to let vanilla behavior continue</li>
     *         </ul>
     */
    EventResult onEnderPearlTeleport(ServerPlayer serverPlayer, Vec3 targetPosition, ThrownEnderpearl thrownEnderpearl, MutableFloat damageAmount, HitResult hitResult);
}
