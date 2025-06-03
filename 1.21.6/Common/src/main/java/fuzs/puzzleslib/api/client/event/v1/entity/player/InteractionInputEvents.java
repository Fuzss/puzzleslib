package fuzs.puzzleslib.api.client.event.v1.entity.player;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.phys.HitResult;

public final class InteractionInputEvents {
    public static final EventInvoker<Attack> ATTACK = EventInvoker.lookup(Attack.class);
    public static final EventInvoker<Use> USE = EventInvoker.lookup(Use.class);
    public static final EventInvoker<Pick> PICK = EventInvoker.lookup(Pick.class);

    private InteractionInputEvents() {
        // NO-OP
    }

    @FunctionalInterface
    public interface Attack {

        /**
         * Runs before the client performs an attack based on {@link Minecraft#hitResult}, either attacking an entity or
         * mining a block (this includes holding to mine a block).
         * <p>
         * This event is placed at the beginning of both {@link net.minecraft.client.Minecraft#startAttack()} and
         * {@link net.minecraft.client.Minecraft#continueAttack(boolean)}.
         *
         * @param minecraft the minecraft singleton instance
         * @param player    the current client player
         * @param hitResult the hit result the interaction is based on
         * @return {@link EventResult#INTERRUPT} to prevent the attack interaction from happening, no packet will be
         *         sent to the server, {@link EventResult#PASS} to allow the attack, the server will be notified for
         *         further processing
         */
        EventResult onAttackInteraction(Minecraft minecraft, LocalPlayer player, HitResult hitResult);
    }

    @FunctionalInterface
    public interface Use {

        /**
         * Runs before the client performs a use interaction based on {@link Minecraft#hitResult}, either using the held
         * item on an entity (like a name tag), on a block (like pushing a button), or using the item itself (like
         * throwing an ender pearl).
         * <p>
         * This event is placed at the beginning of  on Forge, the Fabric implementation is different in that it is
         * spread out through the different use cases in an effort to be able to utilize existing Fabric events.
         *
         * @param minecraft       the minecraft singleton instance
         * @param player          the current client player
         * @param interactionHand the player hand used in this interaction
         * @param hitResult       the hit result the interaction is based on
         * @return {@link EventResult#INTERRUPT} to prevent the use interaction from happening, no packet will be sent
         *         to the server, {@link EventResult#PASS} to allow the use interaction, the server will be notified for
         *         further processing
         */
        EventResult onUseInteraction(Minecraft minecraft, LocalPlayer player, InteractionHand interactionHand, HitResult hitResult);
    }

    @FunctionalInterface
    public interface Pick {

        /**
         * Runs before the client performs a pick interaction based on {@link Minecraft#hitResult}, setting a new item
         * stack to the main hand if possible.
         * <p>
         * This event is placed at the beginning of {@link Minecraft#pickBlock()}.
         *
         * @param minecraft the minecraft singleton instance
         * @param player    the current client player
         * @param hitResult the hit result the interaction is based on
         * @return {@link EventResult#INTERRUPT} to prevent the pick interaction from happening,
         *         {@link EventResult#PASS} to allow the pick interaction
         */
        EventResult onPickInteraction(Minecraft minecraft, LocalPlayer player, HitResult hitResult);
    }
}
