package fuzs.puzzleslib.api.client.event.v1;

import fuzs.puzzleslib.api.event.v1.core.EventInvoker;
import fuzs.puzzleslib.api.event.v1.core.EventResult;
import net.minecraft.client.Minecraft;
import net.minecraft.client.player.LocalPlayer;

public final class InteractionInputEvents {
    public static final EventInvoker<Attack> ATTACK = EventInvoker.lookup(Attack.class);
    public static final EventInvoker<Use> USE = EventInvoker.lookup(Use.class);

    private InteractionInputEvents() {

    }

    @FunctionalInterface
    public interface Attack {

        /**
         * Runs before the client performs an attack based on {@link Minecraft#hitResult}, either attacking an entity or mining a block.
         * <p>This event is placed at the beginning of both {@link net.minecraft.client.Minecraft#startAttack()} and {@link net.minecraft.client.Minecraft#continueAttack(boolean)}.
         *
         * @param minecraft the minecraft singleton instance
         * @param player    the current client player
         * @return {@link EventResult#INTERRUPT} to prevent the attack interaction from happening, no packet will be sent to the server,
         * {@link EventResult#PASS} to allow the attack, the server will be notified for further processing
         */
        EventResult onAttackInteraction(Minecraft minecraft, LocalPlayer player);
    }

    @FunctionalInterface
    public interface Use {

        /**
         * Runs before the client performs a use interaction based on {@link Minecraft#hitResult}, either attacking an entity or mining a block.
         * <p>This event is placed at the beginning of {@link Minecraft#startUseItem()} on Forge,
         * the Fabric implementation is different in that it is spread out through the different use cases in an effort to be able to utilize existing Fabric events
         *
         * @param minecraft the minecraft singleton instance
         * @param player    the current client player
         * @return {@link EventResult#INTERRUPT} to prevent the use interaction from happening, no packet will be sent to the server,
         * {@link EventResult#PASS} to allow the use interaction, the server will be notified for further processing
         */
        EventResult onUseInteraction(Minecraft minecraft, LocalPlayer player);
    }
}
